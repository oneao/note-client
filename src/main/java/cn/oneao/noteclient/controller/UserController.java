package cn.oneao.noteclient.controller;

import cn.oneao.noteclient.enums.ResponseEnums;
import cn.oneao.noteclient.pojo.dto.user.*;
import cn.oneao.noteclient.pojo.entity.User;
import cn.oneao.noteclient.service.UserLogService;
import cn.oneao.noteclient.service.UserService;
import cn.oneao.noteclient.utils.GlobalObjectUtils.UserContext;
import cn.oneao.noteclient.utils.RedisCache;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import cn.oneao.noteclient.utils.SendEmailUtils.SendRegisterEmailUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserLogService userLogService;
    @Autowired
    private SendRegisterEmailUtil sendRegisterEmailUtil;
    @Autowired
    private RedisCache redisCache;
    @Value("${md5encrypt.addSalt.value}")
    private String md5encryptAddSaltValue;
    /**
     * 登录
     * @param userLoginDTO 用户登录
     * @return 返回登录成功或失败，成功则返回
     */
    @PostMapping("/login")
    public Result<Object> userLogin(@RequestBody UserLoginDTO userLoginDTO){
        return userService.userLogin(userLoginDTO.getEmail(), userLoginDTO.getPassword());
    }
    /**
     * 获取验证码
     * @param email：邮箱号
     * @return 返回发生成功是否的信息。
     */
    @GetMapping("/getCode")
    public Result<Object> getCode(@RequestParam("email") String email){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail,email);
        User oneUser = userService.getOne(queryWrapper);
        if (!ObjectUtils.isEmpty(oneUser)){
            return Result.error(ResponseEnums.USER_REGISTER_EMAIL_EXIST);
        }
        if(redisCache.hasKey(email)){
            redisCache.deleteObject(email);
        }
        boolean flag = sendRegisterEmailUtil.sendEmailVerificationCode(email);
        if(flag){
            return Result.success(ResponseEnums.USER_REGISTER_GET_CAPTCHA_SUCCESS);
        }
        return Result.error(ResponseEnums.USER_REGISTER_GET_CAPTCHA_ERR);
    }
    /**
     * 用户注册
     * @param userRegisterDTO:注册用户信息
     * @return 返回成功失败的信息
     */
    @PostMapping("/register")
    public Result<Object> userRegister(@RequestBody UserRegisterDTO userRegisterDTO){
        return userService.userRegister(userRegisterDTO);
    }
    /**
     * 用户退出登录
     * @param userId:用户的id
     * @return 返回信息
     */
    @Transactional
    @GetMapping("/signOut")
    public Result<Object> userSignOut(@RequestHeader(value = "id",required = true)Integer userId){
        if (!ObjectUtils.isEmpty(userId)){
            if(ObjectUtils.isEmpty(UserContext.getUserId())){
                UserContext.setUserId(userId);
            }
            userLogService.saveSignOutLog(userId);
            return Result.success(ResponseEnums.USER_SIGN_OUT_SUCCESS);
        }
        UserContext.removeUserId();
        return Result.error("传入数据为空");
    }
    /**
     * 获取用户的信息
     * @return 返回用户的信息
     */
    @GetMapping("/getUserInfo")
    public Result<Object> getUserInfo(){
        return userService.getUserInfo();
    }
    /**
     * 更新用户信息
     * @param userUpdateDTO 头像 昵称
     * @return 返回
     */
    @PutMapping("/updateUserMessage")
    public Result<Object> updateUserMessage(@RequestBody UserUpdateDTO userUpdateDTO){
        return userService.updateUserMessage(userUpdateDTO);
    }
    /**
     * 获取用户的操作日志（时间线）
     * @return 返回日志信息
     */
    @GetMapping("/getUserTimeLine")
    public Result<Object> getUserTimeLine(){
        return userService.getUserTimeLine();
    }
    /**
     * 重置密码
     * @param userResetPasswordDTO 原密码，新密码
     * @return 返回
     */
    @PutMapping("/toResetPassword")
    public Result<Object> toResetPassword(@RequestBody UserResetPasswordDTO userResetPasswordDTO){
        return userService.toResetPassword(userResetPasswordDTO);
    }
    /**
     * 获取验证码
     * @param email 根据邮箱号获取验证码
     * @return 返回获取成功或失败的信息
     */
    @GetMapping("/getForgetCode")
    public Result<Object> getForgetCode(@RequestParam("email") String email){
        return userService.getForgetCode(email);
    }
    /**
     * 更新忘记密码
     * @param userForgetPasswordDTO 忘记密码DTO
     * @return 返回
     */
    @PutMapping("/updateForgetPassword")
    public Result<Object> updateForgetPassword(@RequestBody UserForgetPasswordDTO userForgetPasswordDTO){
        return userService.updateForgetPassword(userForgetPasswordDTO);
    }
    /**
     * 获得该用户的点赞信息
     * @return 返回
     */
    @GetMapping("/getLikeMessage")
    public Result<Object> getLikeMessage(){
        return userService.getLikeMessage();
    }

    /**
     * 删除一个点赞信息
     * @param value 值
     * @return 返回
     */
    @DeleteMapping("/delOneLikeMessage")
    public Result<Object> delOneLikeMessage(@RequestBody String value){
        return userService.delOneLikeMessage(value);
    }
    /**
     * 删除所有点赞和评论信息
     * @return 返回
     */
    @DeleteMapping("/delAllReplyMessage")
    public Result<Object> delAllReplyMessage(){
        return userService.delAllReplyMessage();
    }
    /**
     * 获取评论信息
     * @return 评论列表
     */
    @GetMapping("/getCommentReply")
    public Result<Object> getCommentReply(){
        return userService.getCommentReply();
    }
    /**
     * 删除一个评论信息
     * @param index 集合的索引
     * @return 没有
     */
    @DeleteMapping("/delOneCommentReply")
    public Result<Object> delOneCommentReply(@RequestParam("index")Integer index){
        return userService.delOneCommentReply(index);
    }
}
