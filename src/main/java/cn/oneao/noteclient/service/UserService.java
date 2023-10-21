package cn.oneao.noteclient.service;

import cn.oneao.noteclient.pojo.dto.UserRegisterDTO;
import cn.oneao.noteclient.pojo.entity.User;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface UserService extends IService<User> {
    Map<String, Object> userLogin(String email, String password);

    Result<Object> userRegister(UserRegisterDTO userRegisterDTO);
}
