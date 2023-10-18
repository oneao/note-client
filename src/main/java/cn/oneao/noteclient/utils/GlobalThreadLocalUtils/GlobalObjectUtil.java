package cn.oneao.noteclient.utils.GlobalThreadLocalUtils;

import cn.oneao.noteclient.pojo.entity.log.SqlActionLog;

import java.util.ArrayList;

public class GlobalObjectUtil {
    private static final GlobalObjectUtil instance = new GlobalObjectUtil();
    private GlobalObject object;

    private GlobalObjectUtil() {
        object = new GlobalObject();
        object.setSqlParams(new ArrayList<>());
        object.setSqlStatements(new ArrayList<>());
        object.setUserId(0);
        object.setSqlActionLog(new SqlActionLog());
    }

    public static GlobalObjectUtil getInstance() {
        return (GlobalObjectUtil) instance;
    }

    public void setObject(GlobalObject obj) {
        object = obj;
    }

    public GlobalObject getObject() {
        return object;
    }

    public void removeObject() {
        object.setSqlParams(new ArrayList<>());
        object.setSqlStatements(new ArrayList<>());
        object.setUserId(0);
        object.setSqlActionLog(new SqlActionLog());
    }
}
