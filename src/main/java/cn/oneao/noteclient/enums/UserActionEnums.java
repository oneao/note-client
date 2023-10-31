package cn.oneao.noteclient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户操作枚举
 */
@Getter
@AllArgsConstructor
public enum UserActionEnums {
    USER_LOGIN("USER_LOGIN","用户登录"),
    USER_REGISTER("USER_REGISTER","用户注册"),
    USER_SIGN_OUT("USER_SIGN_OUT","用户退出登录"),
    USER_UPDATE_MESSAGE("USER_UPDATE_MESSAGE","用户更新个人信息"),
    USER_UPDATE_PASSWORD("USER_UPDATE_PASSWORD","用户更新密码");
    private final String actionName;
    private final String actionDesc;
}
