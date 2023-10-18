package cn.oneao.noteclient.interceptor;

import cn.oneao.noteclient.pojo.entity.log.SqlActionLog;
import cn.oneao.noteclient.utils.GlobalThreadLocalUtils.GlobalObject;
import cn.oneao.noteclient.utils.GlobalThreadLocalUtils.GlobalObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
@Component
public class SqlStatementInterceptor implements Interceptor {
    /**
     * 获取配置中需要拦截的表
     */
    //@Value("#{'${tmall.sync.tables:}'.split(',')}")
    private List<String> tableNames = Arrays.asList("small_note","user");

/*    @Lazy
    @Autowired
    private ISqlLogService sqlLogService;*/

    /**
     * 忽略插入sql_log表的语句
     */
    private static final String IGNORE_SQL_PREFIX = "insert into";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        GlobalObject object =  GlobalObjectUtil.getInstance().getObject();
        List<String> sqlStatements = object.getSqlStatements();
        List<List<String>> sqlParams = object.getSqlParams();
        if (CollectionUtils.isEmpty(tableNames)) {
            //继续执行
            return invocation.proceed();
        }
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        String sql = boundSql.getSql().replaceAll("\\s+", " ").toLowerCase();
        //log.info("sql:{}",sql);

        if (sql.toLowerCase().startsWith(IGNORE_SQL_PREFIX)) {
            //忽略自己 记录 sql 语句入库的sql
            return invocation.proceed();
        }
        //添加sql语句
        if(!sqlStatements.contains(sql)){
            sqlStatements.add(sql);
        }

        List<ParameterMapping> parameterMappings = new ArrayList<>(boundSql.getParameterMappings());
        Object parameterObject = boundSql.getParameterObject();
        if (parameterMappings.isEmpty() && parameterObject == null) {
            log.warn("parameterMappings is empty or parameterObject is null");
            return invocation.proceed();
        }

        Configuration configuration = mappedStatement.getConfiguration();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        try {

            String parameter = "null";
            MetaObject newMetaObject = configuration.newMetaObject(parameterObject);
            List<String> param = new ArrayList<>();
            for (ParameterMapping parameterMapping : parameterMappings) {
                if (parameterMapping.getMode() == ParameterMode.OUT) {
                    continue;
                }
                String propertyName = parameterMapping.getProperty();
                if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    parameter = getParameterValue(parameterObject);
                } else if (newMetaObject.hasGetter(propertyName)) {
                    parameter = getParameterValue(newMetaObject.getValue(propertyName));
                } else if (boundSql.hasAdditionalParameter(propertyName)) {
                    parameter = getParameterValue(boundSql.getAdditionalParameter(propertyName));
                }
                // fixme 此处不严谨，若sql语句中有❓，则替换错位。?️
                log.info("parameter:{}",parameter);
                sql = sql.replaceFirst("\\?", parameter);
                if (!param.contains(parameter)){
                    param.add(parameter);
                }
            }
            sqlParams.add(param);
            // 将拦截到的sql语句插入日志表中
            //log.info("sql======================================:{}",sql);
        } catch (Exception e) {
            log.error(String.format("intercept sql error: [%s]", sql), e);
        }

        return invocation.proceed();
    }

    /**
     * 获取参数
     *
     * @param param Object类型参数
     * @return 转换之后的参数
     */
    private static String getParameterValue(Object param) {
        if (param == null) {
            return "null";
        }
        if (param instanceof Number) {
            return param.toString();
        }
        String value = null;
        if (param instanceof String) {
            value = param.toString();
        } else if (param instanceof Date) {
            //TODO  引入依赖
            //DateUtil.format((Date) param, "yyyy-MM-dd HH:mm:ss");
        } else {
            value = param.toString();
        }
        return StringUtils.quotaMark(value);
    }


    @Override
    public Object plugin(Object o) {
        if (o instanceof StatementHandler) {
            return Plugin.wrap(o, this);
        }
        return o;
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
