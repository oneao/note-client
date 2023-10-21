package cn.oneao.noteclient.utils.GlobalObjectUtils;

import org.springframework.util.ObjectUtils;

public class UserContext {
    private static final ThreadLocal<Integer> userIdThreadLocal = new ThreadLocal<>();

    public static void setUserId(int userId) {
        userIdThreadLocal.set(userId);
    }

    public static int getUserId() {
        Integer userId = userIdThreadLocal.get();
        if (userId == null) {
            userId = -1;
            userIdThreadLocal.set(userId);
        }
        return userId;
    }

    public static void removeUserId() {
        if (ObjectUtils.isEmpty(userIdThreadLocal)) {
            userIdThreadLocal.remove();
        }
    }
}
