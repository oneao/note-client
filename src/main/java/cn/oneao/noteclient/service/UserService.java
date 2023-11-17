package cn.oneao.noteclient.service;

import cn.oneao.noteclient.pojo.dto.user.UserForgetPasswordDTO;
import cn.oneao.noteclient.pojo.dto.user.UserRegisterDTO;
import cn.oneao.noteclient.pojo.dto.user.UserResetPasswordDTO;
import cn.oneao.noteclient.pojo.dto.user.UserUpdateDTO;
import cn.oneao.noteclient.pojo.entity.User;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface UserService extends IService<User> {
    //注册
    Result<Object> userRegister(UserRegisterDTO userRegisterDTO);
    //登录
    Result<Object> userLogin(String email, String password);
    //获取用户基本信息
    Result<Object> getUserInfo();
    //更新用户信息
    Result<Object> updateUserMessage(UserUpdateDTO userUpdateDTO);
    //获取用户的时间线信息
    Result<Object> getUserTimeLine();
    //更新密码
    Result<Object> toResetPassword(UserResetPasswordDTO userResetPasswordDTO);
    //获取忘记密码中的验证码
    Result<Object> getForgetCode(String email);
    //根据忘记密码验证码更新
    Result<Object> updateForgetPassword(UserForgetPasswordDTO userForgetPasswordDTO);
    //获取个人的点赞信息
    Result<Object> getLikeMessage();
    //删除一个点赞信息
    Result<Object> delOneLikeMessage(String value);
    //删除所有点赞信息
    Result<Object> delAllReplyMessage();
    //获取评论信息
    Result<Object> getCommentReply();
    //删除一个评论信息
    Result<Object> delOneCommentReply(Integer index);
}
