package cn.oneao.noteclient.controller;

import cn.oneao.noteclient.enums.ResponseEnums;
import cn.oneao.noteclient.pojo.dto.UserLoginDTO;
import cn.oneao.noteclient.pojo.dto.UserRegisterDTO;
import cn.oneao.noteclient.pojo.entity.User;
import cn.oneao.noteclient.service.UserLogService;
import cn.oneao.noteclient.service.UserService;
import cn.oneao.noteclient.utils.Result;
import cn.oneao.noteclient.utils.sendEmailUtils.SendRegisterEmailUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 登录
     * @param userLoginDTO 用户登录
     * @return 返回登录成功或失败，成功则返回
     */
    @PostMapping("/login")
    public Result<Object> userLogin(@RequestBody UserLoginDTO userLoginDTO){
        Map<String, Object> map = userService.userLogin(userLoginDTO.getEmail(), userLoginDTO.getPassword());
        if(map.isEmpty()){
            return Result.success(ResponseEnums.USER_LOGIN_ERROR);
        }
        return Result.success(map,ResponseEnums.USER_LOGIN_SUCCESS);
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
            userLogService.saveSignOutLog(userId);
            return Result.success(ResponseEnums.USER_SIGN_OUT_SUCCESS);
        }
        return Result.error("传入数据为空");
    }
}
