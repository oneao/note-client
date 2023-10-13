package cn.oneao.noteclient.service.impl;

import cn.oneao.noteclient.enums.UserActionEnums;
import cn.oneao.noteclient.mapper.UserLogMapper;
import cn.oneao.noteclient.pojo.entity.UserLog;
import cn.oneao.noteclient.service.UserLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserLogServiceImpl extends ServiceImpl<UserLogMapper, UserLog> implements UserLogService {
    @Override
    @Transactional
    public void saveSignOutLog(Integer userId) {
        UserLog userLog = new UserLog();
        userLog.setUserId(userId);
        userLog.setAction(UserActionEnums.USER_SIGN_OUT.getActionName());
        userLog.setActionDesc(UserActionEnums.USER_SIGN_OUT.getActionDesc());
        this.save(userLog);
    }
}
