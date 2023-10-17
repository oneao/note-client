package cn.oneao.noteclient.aspects;

import cn.oneao.noteclient.mapper.SqlActionLogMapper;
import cn.oneao.noteclient.pojo.entity.log.SqlActionLog;
import cn.oneao.noteclient.utils.GlobalThreadLocalUtils.GlobalObject;
import cn.oneao.noteclient.utils.GlobalThreadLocalUtils.GlobalObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;


@Aspect
@Component
@Slf4j
public class SqlExecutionTimeAspect {
    @Autowired
    private SqlActionLogMapper sqlActionLogMapper;
    @Transactional
    @Around("execution(* cn.oneao.noteclient.service.*.*(..))")
    public Object measureSqlExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return measureSqlExecutionTimeInternal(joinPoint);
    }
    @Transactional
    @Around("@annotation(cn.oneao.noteclient.annotations.LogSqlExecution)")
    public Object measureSqlExecutionTimeWithAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        return measureSqlExecutionTimeInternal(joinPoint);
    }
    public Object measureSqlExecutionTimeInternal(ProceedingJoinPoint joinPoint) throws Throwable {
        GlobalObject globalObject = (GlobalObject) GlobalObjectUtil.getInstance().getObject();

        long startTime = System.currentTimeMillis();//开始时间
        Object proceed = joinPoint.proceed();//执行
        if (ObjectUtils.isEmpty(globalObject)){
            return proceed;
        }
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();//方法名
        long endTime = System.currentTimeMillis();//结束时间

        //获取日志对象
        SqlActionLog sqlActionLog = globalObject.getSqlActionLog();
        sqlActionLog.setSqlInterfaceName(methodName);
        sqlActionLog.setExecutionTime(endTime - startTime);
        //获取用户id
        Integer userId = globalObject.getUserId();
        sqlActionLog.setUserId(userId);
        //获取当前线程的sql语句
        List<String> sqlStatements = globalObject.getSqlStatements();
        for (String sqlStatement : sqlStatements) {
            sqlActionLog.setSqlStatement(sqlStatement);
            sqlActionLogMapper.insert(sqlActionLog);
        }
        GlobalObjectUtil.getInstance().removeObject();
        //移除后重新创建
        GlobalObject newGlobalObject = new GlobalObject();
        newGlobalObject.setUserId(userId);
        newGlobalObject.setSqlStatements(new ArrayList<>());
        newGlobalObject.setSqlActionLog(new SqlActionLog());
        GlobalObjectUtil.getInstance().setObject(newGlobalObject);
        return proceed;
    }
}
