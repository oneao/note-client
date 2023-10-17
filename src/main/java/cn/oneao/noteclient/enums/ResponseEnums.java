package cn.oneao.noteclient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
//captcha
public enum ResponseEnums {
    //用户相关
    USER_TOKEN_ERR(50000,"尚未登录或登录已过期,请重新登录"),
    USER_LOGIN_ERROR(50001,"邮箱号或密码错误或未注册"),
    USER_REGISTER_EMAIL_EXIST(50002,"该邮箱号已经注册过了哦!"),
    USER_REGISTER_GET_CAPTCHA_ERR(50003,"验证码获取失败,请联系管理员"),
    USER_REGISTER_CAPTCHA_OVERDUE(50004,"验证码不存在或已过期，请重新获取验证码"),
    USER_REGISTER_CAPTCHA_ERR(50005,"验证码错误"),
    USER_LOGIN_SUCCESS(60000,"登录成功"),
    USER_REGISTER_GET_CAPTCHA_SUCCESS(60001,"验证码已发送"),
    USER_REGISTER_SUCCESS(60002,"注册成功"),
    USER_SIGN_OUT_SUCCESS(60003,"注销成功"),
    //小记操作
    SmallNote_UPDATE_STATUS_SUCCESS(60004,"更新成功"),
    SmallNote_DELETE_LOGIC_SUCCESS(60005,"删除成功,可在回收站内恢复"),
    SmallNote_ADD_SUCCESS(60006,"新增小记成功"),
    SmallNote_UPDATE_SUCCESS(60007,"更新小记成功");
    private final Integer code;   //响应码
    private final String msg; //相应信息
}
