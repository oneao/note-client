package cn.oneao.noteclient.service.impl;

import cn.oneao.noteclient.mapper.UserLevelMapper;
import cn.oneao.noteclient.pojo.entity.UserLevel;
import cn.oneao.noteclient.service.UserLevelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserLevelServiceImpl extends ServiceImpl<UserLevelMapper, UserLevel> implements UserLevelService {
}
