package cn.oneao.noteclient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
//captcha
public enum ResponseEnums {
    //统一操作
    PARAMETER_MISSING(40000,"参数缺失"),
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
    SmallNote_UPDATE_SUCCESS(60007,"更新小记成功"),
    //笔记操作
    NOTE_VERIFY_LOCK_SUCCESS(60008,"笔记密码正确"),
    NOTE_VERIFY_LOCK_ERROR(60009,"笔记密码错误"),
    NOTE_TOP_UPDATE_SUCCESS(60010,"笔记置顶状态修改成功"),
    NOTE_DELETE_LOGIC_SUCCESS(60011,"删除成功,可在回收站内恢复"),
    NOTE_DELETE_COMPLETE_SUCCESS(60012,"删除成功,注意无法恢复!"),
    NOTE_ADD_SUCCESS(60013,"新增笔记成功!"),
    NOTE_ADD_LOCK_SUCCESS(60014,"为该笔记添加密码成功!"),
    NOTE_DELETE_LOCK_SUCCESS(60015,"删除笔记密码成功!"),
    NOTE_NEED_PASSWORD(60016,"该笔记需要密码访问"),
    NOTE_UPDATE_SUCCESS(60017,"更新成功"),
    NOTE_COLLECTION_SUCCESS(60018,"收藏笔记成功"),
    NOTE_COLLECTION_CANCEL_SUCCESS(60019,"取消收藏笔记成功"),
    NOTE_SHARE_SUCCESS(60020,"分享笔记成功"),
    NOTE_SHARE_ERROR(60021,"该笔记已分享,如需要取消,请去分享中心!"),
    NOTE_SHARE_OVER(60022,"该笔记分享不存在已过期,无法查看!"),
    NOTE_SHARE_NOT_EXISTS(60023,"不存在该分享笔记"),
    NOTE_SHARE_NEED_LOCK(60024,"该分享笔记需要锁"),
    NOTE_SHARE_NOT_NEED_LOCK(60025,"该笔记不需要锁"),
    NOTE_SHARE_LOCK_PASSWORD_ERROR(60026,"笔记分享密码错误，无法访问"),
    NOTE_SHARE_GET_SUCCESS(60027,"获取分享笔记成功!"),
    NOTE_SHARE_ALLOW_ADD(60028,"允许添加分享笔记"),
    NOTE_SHARE_NOT_ALLOW_ADD(60029,"该笔记已经分享过了哦,请勿重新分享!"),
    NOTE_SHARE_LICK_SUCCESS(60030,"点赞成功"),
    NOTE_SHARE_LICK_CANCEL_SUCCESS(60031,"取消点赞成功");
    private final Integer code;   //响应码
    private final String msg; //相应信息
}
