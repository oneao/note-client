package cn.oneao.noteclient.service;

import cn.oneao.noteclient.pojo.entity.UserLog;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserLogService extends IService<UserLog> {
    void saveSignOutLog(Integer userId);
}
