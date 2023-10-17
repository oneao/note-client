package cn.oneao.noteclient.utils.GlobalThreadLocalUtils;

public class GlobalObjectUtil<T> {
    private static final GlobalObjectUtil<?> instance = new GlobalObjectUtil<>();
    private T object;

    private GlobalObjectUtil() {
    }

    public static <T> GlobalObjectUtil<T> getInstance() {
        return (GlobalObjectUtil<T>) instance;
    }

    public void setObject(T obj) {
        this.object = obj;
    }

    public T getObject() {
        return object;
    }

    public void removeObject() {
        this.object = null;
    }
}


