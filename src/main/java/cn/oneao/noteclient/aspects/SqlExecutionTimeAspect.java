package cn.oneao.noteclient.aspects;

import cn.oneao.noteclient.mapper.SqlActionLogMapper;
import cn.oneao.noteclient.pojo.entity.log.SqlActionLog;
import cn.oneao.noteclient.utils.GlobalObjectUtils.GlobalObject;
import cn.oneao.noteclient.utils.GlobalObjectUtils.GlobalObjectUtil;
import cn.oneao.noteclient.utils.GlobalObjectUtils.UserContext;
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
        if (!ObjectUtils.isEmpty(UserContext.getUserId())) {
            int userId = (int) UserContext.getUserId();
            sqlActionLog.setUserId(userId);
        }
        //获取当前线程的sql语句
        List<String> sqlStatements = globalObject.getSqlStatements();
        //判空
        if(ObjectUtils.isEmpty(sqlStatements)){
            return proceed;
        }
        //修改sql语句格式
        String sqlStatement = changeSqlStatement(sqlStatements);
        //获取sql语句中的参数
        List<List<String>> sqlParams = globalObject.getSqlParams();
        //对参数进行去重
        String sqlParam = removeParmaDuplicates(sqlParams);
        //赋值
        sqlActionLog.setSqlParams(sqlParam);
        sqlActionLog.setSqlStatements(sqlStatement);
        //插入
        sqlActionLogMapper.insert(sqlActionLog);
        //移除
        GlobalObjectUtil.getInstance().removeObject();
        //移除后重新创建
        GlobalObject newGlobalObject = new GlobalObject();
        newGlobalObject.setUserId(0);
        newGlobalObject.setSqlStatements(new ArrayList<>());
        newGlobalObject.setSqlActionLog(new SqlActionLog());
        newGlobalObject.setSqlParams(new ArrayList<>());
        GlobalObjectUtil.getInstance().setObject(newGlobalObject);
        return proceed;
    }
    //数组去重
    public String changeSqlStatement(List<String> nestedList){
        if (nestedList.size() == 1){
            return nestedList.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < nestedList.size(); i++) {
            if(i == 0){
                stringBuilder.append("[").append(nestedList.get(i)).append("|||");
            }else if(i == nestedList.size() - 1){
                stringBuilder.append(nestedList.get(nestedList.size() - 1)).append("]");
            }else {
                stringBuilder.append(nestedList.get(i)).append("|||");
            }
        }
        return stringBuilder.toString();
    }
    //参数去重
    public String removeParmaDuplicates(List<List<String>> nestedList) {
        List<List<String>> newList = new ArrayList<>();
        int i = 1;
        for (List<String> list : nestedList) {
            if(i % 2 != 0){
                newList.add(list);
            }
            i++;
        }
        return newList.toString();
    }
}
