package cn.oneao.noteclient.service.impl;

import cn.oneao.noteclient.constant.RedisKeyConstant;
import cn.oneao.noteclient.enums.NoteActionEnums;
import cn.oneao.noteclient.enums.ResponseEnums;
import cn.oneao.noteclient.enums.UserActionEnums;
import cn.oneao.noteclient.mapper.NoteLogMapper;
import cn.oneao.noteclient.mapper.NoteMapper;
import cn.oneao.noteclient.mapper.UserMapper;
import cn.oneao.noteclient.pojo.dto.user.UserForgetPasswordDTO;
import cn.oneao.noteclient.pojo.dto.user.UserRegisterDTO;
import cn.oneao.noteclient.pojo.dto.user.UserResetPasswordDTO;
import cn.oneao.noteclient.pojo.dto.user.UserUpdateDTO;
import cn.oneao.noteclient.pojo.entity.User;
import cn.oneao.noteclient.pojo.entity.UserLevel;
import cn.oneao.noteclient.pojo.entity.log.UserLog;
import cn.oneao.noteclient.pojo.entity.rabbitmq.RMCommentReplyNotice;
import cn.oneao.noteclient.pojo.vo.RecentOperationNoteVO;
import cn.oneao.noteclient.pojo.vo.UserInfoVO;
import cn.oneao.noteclient.pojo.vo.UserLoginVO;
import cn.oneao.noteclient.pojo.vo.UserTimeLineVO;
import cn.oneao.noteclient.service.NoteLogService;
import cn.oneao.noteclient.service.UserLevelService;
import cn.oneao.noteclient.service.UserLogService;
import cn.oneao.noteclient.service.UserService;
import cn.oneao.noteclient.utils.GlobalObjectUtils.UserContext;
import cn.oneao.noteclient.utils.JwtHelper;
import cn.oneao.noteclient.utils.RedisCache;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import cn.oneao.noteclient.utils.SendEmailUtils.SendForgetCodeEmailUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserLogService userLogService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private NoteLogMapper noteLogMapper;
    @Value("${md5encrypt.addSalt.value}")
    private String md5encryptAddSaltValue;
    @Autowired
    private SendForgetCodeEmailUtil sendForgetCodeEmailUtil;
    @Autowired
    private UserLevelService userLevelService;
    @Override
    @Transactional
    public Result<Object> userRegister(UserRegisterDTO userRegisterDTO) {
        String email = userRegisterDTO.getEmail();
        String redisKey = "USER_REGISTER_" + email;
        //验证码不存在，或者已经过期。
        boolean flag = redisCache.hasKey(redisKey);
        //Boolean flag = redisTemplate.hasKey(email);
        if(Boolean.FALSE.equals(flag)){
            return Result.error(ResponseEnums.USER_REGISTER_CAPTCHA_OVERDUE);
        }
        //String code = (String) redisTemplate.opsForValue().get(email);
        String code = (String) redisCache.getCacheObject(redisKey);
        if(!ObjectUtils.isEmpty(code) && !code.equals(userRegisterDTO.getCode())){
            return Result.error(ResponseEnums.USER_REGISTER_CAPTCHA_ERR);
        }
        User user = new User();
        user.setEmail(email);
        user.setNickName(email);
        user.setAvatar("http://127.0.0.1:9000/note/adf77c91-1fe2-4610-971c-c033b9b62242.jpg");
        //采用md5加密
        user.setPassword(DigestUtils.md5DigestAsHex((md5encryptAddSaltValue+userRegisterDTO.getPassword()).getBytes(StandardCharsets.UTF_8)));
        //新增返回的用户id
        userMapper.insert(user);
        //删除redis中注册验证码
        redisCache.deleteObject(redisKey);
        Integer userId = user.getId();
        //插入UserLevel表
        UserLevel userLevel = new UserLevel();
        userLevel.setUserId(userId);
        userLevel.setLevel(1);
        userLevel.setCollectionNoteNumber(0);
        userLevel.setShareNoteNumber(0);
        userLevel.setShareNoteVisitNumber(0);
        userLevel.setShareNoteLikeNumber(0);
        userLevel.setShareNoteCommentNumber(0);
        userLevelService.save(userLevel);
        //添加操作日志
        UserLog userLog = new UserLog();
        userLog.setUserId(userId);
        userLog.setAction(UserActionEnums.USER_REGISTER.getActionName());
        userLog.setActionDesc(UserActionEnums.USER_REGISTER.getActionDesc()+":"+user.getEmail());
        userLogService.save(userLog);
        return Result.success(ResponseEnums.USER_REGISTER_SUCCESS);
    }
    @Override
    public Result<Object> userLogin(String email, String password) {
        Map<String, Object> map = new HashMap<>();

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail,email);
        String userPassword = DigestUtils.md5DigestAsHex((md5encryptAddSaltValue+password).getBytes(StandardCharsets.UTF_8));
        queryWrapper.eq(User::getPassword,userPassword);
        queryWrapper.eq(User::getIsDelete,0);
        User user = userMapper.selectOne(queryWrapper);
        if (user.getStatus() == 1){
            //账号禁用
            return Result.error(ResponseEnums.USER_ACCOUNT_DISABLE);
        }

        if(!ObjectUtils.isEmpty(user)){
            UserContext.setUserId(user.getId());
            UserLog userLog = new UserLog();
            userLog.setUserId(user.getId());
            userLog.setAction(UserActionEnums.USER_LOGIN.getActionName());
            userLog.setActionDesc(UserActionEnums.USER_LOGIN.getActionDesc());
            userLogService.save(userLog);
            UserLoginVO userLoginVO = new UserLoginVO();
            BeanUtils.copyProperties(user,userLoginVO);
            map.put("token",JwtHelper.createToken(user.getId()));
            map.put("user",userLoginVO);
            return Result.success(map,ResponseEnums.USER_LOGIN_SUCCESS);
        }
        return Result.success(ResponseEnums.USER_LOGIN_ERROR);
    }
    //获取用户基本信息
    @Override
    public Result<Object> getUserInfo() {
        int userId = UserContext.getUserId();
        User user = this.getById(userId);
        if (ObjectUtils.isEmpty(user)){
            return Result.error(ResponseEnums.UNKNOWN_ERROR);
        }
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user,userInfoVO);
        return Result.success(userInfoVO);
    }
    //更新用户信息
    @Override
    public Result<Object> updateUserMessage(UserUpdateDTO userUpdateDTO) {
        int userId = UserContext.getUserId();
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId,userId);
        updateWrapper.set(User::getNickName,userUpdateDTO.getNickName());
        updateWrapper.set(User::getAvatar,userUpdateDTO.getAvatar());
        this.update(new User(),updateWrapper);
        UserLog userLog = new UserLog();
        userLog.setUserId(userId);
        userLog.setAction(UserActionEnums.USER_UPDATE_MESSAGE.getActionName());
        userLog.setActionDesc(UserActionEnums.USER_UPDATE_MESSAGE.getActionDesc());
        userLogService.save(userLog);
        return Result.success(ResponseEnums.USER_UPDATE_MESSAGE);
    }
    //获取用户的时间线信息
    @Override
    public Result<Object> getUserTimeLine() {
        List<UserTimeLineVO> resultList = new ArrayList<>();
        int userId = UserContext.getUserId();
        List<UserTimeLineVO> noteActionMessage = noteLogMapper.getNoteActionMessage(userId);
        for (UserTimeLineVO userTimeLineVO : noteActionMessage) {
            switch (userTimeLineVO.getTitle()) {
                case "USER_ADD_NOTE" -> userTimeLineVO.setTitle("新增笔记");
                case "LOGIC_DELETE_NOTE" -> userTimeLineVO.setTitle("删除笔记(可在回收站内恢复)");
                case "COMPLETE_DELETE_NOTE" -> {
                    userTimeLineVO.setTitle("删除笔记(不可在回收站内恢复)");
                    userTimeLineVO.setNoteTitle("彻底删除无法查看标题");
                    userTimeLineVO.setNoteBody("彻底删除无法查看主体");
                }
                case "USER_RECOVER_NOTE" -> userTimeLineVO.setTitle("恢复笔记");
                case "DELETE_NOTE_MANY" -> {
                    userTimeLineVO.setTitle("批量删除笔记(不可在回收站内恢复)");
                    userTimeLineVO.setNoteTitle("批量删除笔记");
                    userTimeLineVO.setNoteBody("彻底删除笔记");
                }
                case "RECOVER_NOTE_MANY" -> {
                    userTimeLineVO.setTitle("批量恢复笔记(快去笔记列表查看吧!)");
                    userTimeLineVO.setNoteTitle("批量恢复笔记");
                    userTimeLineVO.setNoteBody("彻底恢复笔记");
                }
            }
            if (ObjectUtils.isEmpty(userTimeLineVO.getNoteTitle())){
                userTimeLineVO.setNoteTitle("该笔记暂无标题哦，快去设置标题吧!");
            }
            if (ObjectUtils.isEmpty(userTimeLineVO.getNoteBody())){
                userTimeLineVO.setNoteBody("该笔记暂无内容哦，快去添加吧！");
            }
            resultList.add(userTimeLineVO);
        }
        List<UserTimeLineVO> smallNoteActionMessage = noteLogMapper.getSmallNoteActionMessage(userId);
        for (UserTimeLineVO userTimeLineVO : smallNoteActionMessage) {
            switch (userTimeLineVO.getTitle()){
                case "USER_ADD_SMALL_NOTE" -> userTimeLineVO.setTitle("新增小记");
                case "LOGIC_DELETE_SMALL_NOTE" -> userTimeLineVO.setTitle("删除小记(可在回收站内恢复)");
                case "COMPLETE_DELETE_SMALL_NOTE" -> {
                    userTimeLineVO.setTitle("删除小记(不可在回收站内恢复)");
                    userTimeLineVO.setNoteTitle("彻底删除无法查看标题");
                    userTimeLineVO.setNoteBody("彻底删除无法查看备注");
                }
                case "USER_RECOVER_SMALL_NOTE" -> userTimeLineVO.setTitle("恢复小记");
                case "DELETE_SMALL_NOTE_MANY" -> {
                    userTimeLineVO.setTitle("批量删除小记(不可在回收站内恢复)");
                    userTimeLineVO.setNoteTitle("彻底删除小记");
                    userTimeLineVO.setNoteBody("彻底删除小记");
                }
                case "RECOVER_SMALL_NOTE_MANY" -> {
                    userTimeLineVO.setTitle("批量恢复小记(快去笔记列表查看吧!)");
                    userTimeLineVO.setNoteTitle("彻底恢复小记");
                    userTimeLineVO.setNoteBody("彻底恢复小记");
                }
            }
            if (ObjectUtils.isEmpty(userTimeLineVO.getNoteTitle())){
                userTimeLineVO.setNoteTitle("该小记暂无标题哦，快去设置标题吧!");
            }
            if (ObjectUtils.isEmpty(userTimeLineVO.getNoteBody())){
                userTimeLineVO.setNoteBody("该小记暂无备注哦，快去添加吧！");
            }
            resultList.add(userTimeLineVO);
        }
        return Result.success(resultList);
    }
    //重置密码
    @Override
    public Result<Object> toResetPassword(UserResetPasswordDTO userResetPasswordDTO) {
        String oldPassword = userResetPasswordDTO.getOldPassword();
        String newPassword = userResetPasswordDTO.getNewPassword();
        if (ObjectUtils.isEmpty(oldPassword) || ObjectUtils.isEmpty(newPassword)){
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        String userOldPassword = DigestUtils.md5DigestAsHex((md5encryptAddSaltValue + oldPassword).getBytes(StandardCharsets.UTF_8));
        int userId = UserContext.getUserId();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId,userId);
        queryWrapper.eq(User::getPassword,userOldPassword);
        User user = this.getOne(queryWrapper);
        //如果原密码错误
        if (ObjectUtils.isEmpty(user)){
            return Result.error(ResponseEnums.USER_UPDATE_PASSWORD_ERROR);
        }
        //如果没错的话就更改
        String userNewPassword = DigestUtils.md5DigestAsHex((md5encryptAddSaltValue + newPassword).getBytes(StandardCharsets.UTF_8));
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId,userId);
        updateWrapper.set(User::getPassword,userNewPassword);
        this.update(new User(),updateWrapper);
        //日子
        UserLog userLog = new UserLog();
        userLog.setUserId(userId);
        userLog.setAction(UserActionEnums.USER_UPDATE_PASSWORD.getActionName());
        userLog.setActionDesc(UserActionEnums.USER_UPDATE_PASSWORD.getActionDesc());
        userLogService.save(userLog);
        return Result.success(ResponseEnums.USER_UPDATE_PASSWORD_SUCCESS);
    }
    //获取忘记密码中的验证码
    @Override
    public Result<Object> getForgetCode(String email) {
        if (ObjectUtils.isEmpty(email)){
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        String redisKey = "FORGET_PASSWORD_" +email;
        if (redisCache.hasKey(redisKey)){
            redisCache.deleteObject(redisKey);
        }
        //查询邮箱是不是被恶意更改
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId,UserContext.getUserId());
        queryWrapper.eq(User::getEmail,email);
        User user = this.getOne(queryWrapper);
        if(ObjectUtils.isEmpty(user)){
            return Result.error(ResponseEnums.UNKNOWN_ERROR);
        }
        //发送右键
        boolean flag = sendForgetCodeEmailUtil.sendEmailVerificationCode(email);
        if (flag) {
            return Result.success(ResponseEnums.USER_FORGET_GET_CAPTCHA_SUCCESS);
        }else {
            return Result.error(ResponseEnums.USER_FORGET_GET_CAPTCHA_ERROR);
        }
    }
    //忘记密码更新
    @Override
    @Transactional
    public Result<Object> updateForgetPassword(UserForgetPasswordDTO userForgetPasswordDTO) {
        String email = userForgetPasswordDTO.getEmail();
        String code = userForgetPasswordDTO.getCode();//验证码
        String newPassword = userForgetPasswordDTO.getNewPassword();//新密码
        if (ObjectUtils.isEmpty(email) || ObjectUtils.isEmpty(code) || ObjectUtils.isEmpty(newPassword)){
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        String redisKey = "FORGET_PASSWORD_" + email;
        //redis中不存在验证码
        if (!redisCache.hasKey(redisKey)){
            return Result.error(ResponseEnums.USER_REGISTER_CAPTCHA_OVERDUE);//验证不存在或过期
        }
        //验证码错误
        String redisValue = redisCache.getCacheObject(redisKey);
        if (!code.equals(redisValue)){
            return Result.error(ResponseEnums.USER_REGISTER_CAPTCHA_ERR);
        }
        //查询
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId,UserContext.getUserId());
        queryWrapper.eq(User::getEmail,email);
        User user = this.getOne(queryWrapper);
        if(ObjectUtils.isEmpty(user)){
            return Result.error(ResponseEnums.UNKNOWN_ERROR);
        }
        String newPasswordMd5 = DigestUtils.md5DigestAsHex((md5encryptAddSaltValue + newPassword).getBytes(StandardCharsets.UTF_8));
        //更新
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId,UserContext.getUserId());
        updateWrapper.set(User::getPassword,newPasswordMd5);
        this.update(new User(),updateWrapper);
        //删除key
        redisCache.deleteObject(redisKey);
        //日志
        UserLog userLog = new UserLog();
        userLog.setUserId(UserContext.getUserId());
        userLog.setAction(UserActionEnums.USER_UPDATE_MESSAGE.getActionName());
        userLog.setActionDesc(UserActionEnums.USER_UPDATE_MESSAGE.getActionDesc());
        userLogService.save(userLog);
        return Result.success(ResponseEnums.USER_UPDATE_PASSWORD_SUCCESS);
    }
    //获取个人的点赞信息
    @Override
    public Result<Object> getLikeMessage() {
        String redisKey = RedisKeyConstant.SHARE_NOTE_LIKE_MESSAGE_UID + UserContext.getUserId();
        List<String> result = new ArrayList<>();
        if (redisCache.hasKey(redisKey)){
            result = redisCache.getCacheList(redisKey);
        }
        return Result.success(result);
    }
    //删除一个通知栏点赞信息
    @Override
    public Result<Object> delOneLikeMessage(String value) {
        String redisKey = RedisKeyConstant.SHARE_NOTE_LIKE_MESSAGE_UID + UserContext.getUserId();
        if (redisCache.hasKey(redisKey)) {
            List<String> cacheList = redisCache.getCacheList(redisKey);
            if (cacheList.contains(value)) {
                redisCache.deleteCacheListValue(redisKey, 0, value);
                return Result.success();
            } else {
                return Result.error(ResponseEnums.UNKNOWN_ERROR);
            }
        }else {
            return Result.error(ResponseEnums.UNKNOWN_ERROR);
        }
    }
    //删除所有通知栏点赞信息
    @Override
    public Result<Object> delAllReplyMessage() {
        String redisLikeKey = RedisKeyConstant.SHARE_NOTE_LIKE_MESSAGE_UID + UserContext.getUserId();
        if (redisCache.hasKey(redisLikeKey)){
            redisCache.deleteObject(redisLikeKey);
        }
        String redisCommentKey = RedisKeyConstant.SHARE_NOTE_COMMENT_UID + UserContext.getUserId();
        if(redisCache.hasKey(redisCommentKey)){
            redisCache.deleteObject(redisCommentKey);
        }
        return Result.success();
    }
    //获取所有通知栏评论信息
    @Override
    public Result<Object> getCommentReply() {
        String redisKey = RedisKeyConstant.SHARE_NOTE_COMMENT_UID + UserContext.getUserId();
        List<String> result = new ArrayList<>();
        if (redisCache.hasKey(redisKey)){
            result = redisCache.getCacheList(redisKey);
        }
        return Result.success(result);
    }
    //删除一个通知栏点赞信息
    @Override
    public Result<Object> delOneCommentReply(Integer index) {
        if(ObjectUtils.isEmpty(index)){
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        String redisKey = RedisKeyConstant.SHARE_NOTE_COMMENT_UID + UserContext.getUserId();
        if(redisCache.hasKey(redisKey)) {
            List<RMCommentReplyNotice> cacheList = redisCache.getCacheList(redisKey);
            RMCommentReplyNotice rmCommentReplyNotice = cacheList.get(index);
            redisCache.deleteCacheListValue(redisKey,1,rmCommentReplyNotice);
        }
        return Result.success();
    }
    //获取最近操作的笔记和小记
    @Override
    public Result<Object> getRecentOperationNote() {
        int userId = UserContext.getUserId();
        if(ObjectUtils.isEmpty(userId) || userId == -1){
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        List<RecentOperationNoteVO> list = userMapper.getRecentOperationNote(userId);
        return Result.success(list);
    }
}
