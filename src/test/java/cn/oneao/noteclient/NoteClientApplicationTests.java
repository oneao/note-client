package cn.oneao.noteclient;

import cn.oneao.noteclient.constant.FileConstants;
import cn.oneao.noteclient.pojo.entity.User;
import cn.oneao.noteclient.pojo.vo.UserLoginVO;
import cn.oneao.noteclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;


@SpringBootTest
@Slf4j
class NoteClientApplicationTests {
    @Autowired
    private UserService userService;
    @Test
    void contextLoads() {
        String userAvatar = FileConstants.USER_AVATAR;
        System.out.println(userAvatar);
    }
}
