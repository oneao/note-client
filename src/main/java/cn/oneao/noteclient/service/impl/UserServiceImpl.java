package cn.oneao.noteclient.service.impl;

import cn.oneao.noteclient.enums.ResponseEnums;
import cn.oneao.noteclient.enums.UserActionEnums;
import cn.oneao.noteclient.mapper.UserMapper;
import cn.oneao.noteclient.pojo.dto.UserRegisterDTO;
import cn.oneao.noteclient.pojo.entity.User;
import cn.oneao.noteclient.pojo.entity.log.UserLog;
import cn.oneao.noteclient.pojo.vo.UserLoginVO;
import cn.oneao.noteclient.service.UserLogService;
import cn.oneao.noteclient.service.UserService;
import cn.oneao.noteclient.utils.JwtHelper;
import cn.oneao.noteclient.utils.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserLogService userLogService;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public Map<String, Object> userLogin(String email, String password) {
        Map<String, Object> map = new HashMap<>();

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail,email);
        queryWrapper.eq(User::getPassword,DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)));
        queryWrapper.eq(User::getIsDelete,0);
        User user = userMapper.selectOne(queryWrapper);

        if(!ObjectUtils.isEmpty(user)){
            UserLog userLog = new UserLog();
            userLog.setUserId(user.getId());
            userLog.setAction(UserActionEnums.USER_LOGIN.getActionName());
            userLog.setActionDesc(UserActionEnums.USER_LOGIN.getActionDesc());
            userLogService.save(userLog);
            UserLoginVO userLoginVO = new UserLoginVO();
            BeanUtils.copyProperties(user,userLoginVO);
            map.put("token",JwtHelper.createToken(user.getId()));
            map.put("user",userLoginVO);
        }
        return map;
    }
    @Override
    @Transactional
    public Result<Object> userRegister(UserRegisterDTO userRegisterDTO) {
        String email = userRegisterDTO.getEmail();
        //验证码不存在，或者已经过期。
        Boolean flag = redisTemplate.hasKey(email);
        if(Boolean.FALSE.equals(flag)){
            return Result.error(ResponseEnums.USER_REGISTER_CAPTCHA_OVERDUE);
        }
        String code = (String) redisTemplate.opsForValue().get(email);
        if(!ObjectUtils.isEmpty(code) && !code.equals(userRegisterDTO.getCode())){
            return Result.error(ResponseEnums.USER_REGISTER_CAPTCHA_ERR);
        }
        User user = new User();
        user.setEmail(email);
        user.setNickName(email);
        user.setAvatar("/img/userAvatar/DefaultAvatar.png");
        //采用md5加密
        user.setPassword(DigestUtils.md5DigestAsHex(userRegisterDTO.getPassword().getBytes(StandardCharsets.UTF_8)));
        //TODO:头像位置待添加
        //新增返回的用户id
        userMapper.insert(user);
        Integer userId = user.getId();
        //添加操作日志
        UserLog userLog = new UserLog();
        userLog.setUserId(userId);
        userLog.setAction(UserActionEnums.USER_REGISTER.getActionName());
        userLog.setActionDesc(UserActionEnums.USER_REGISTER.getActionDesc()+":"+user.getEmail());
        userLogService.save(userLog);
        return Result.success(ResponseEnums.USER_REGISTER_SUCCESS);
    }
}
