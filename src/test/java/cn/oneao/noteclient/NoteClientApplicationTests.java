package cn.oneao.noteclient;

import cn.oneao.noteclient.constant.RedisKeyConstant;
import cn.oneao.noteclient.pojo.entity.User;
import cn.oneao.noteclient.utils.GlobalObjectUtils.UserContext;
import cn.oneao.noteclient.utils.HttpClientUtil;
import cn.oneao.noteclient.utils.RedisCache;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.List;


@SpringBootTest(classes = NoteClientApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class NoteClientApplicationTests {
    @Autowired
    private RedisCache redisCache;
    @Test
    void contextLoads() {
        User user = new User();
        user.setPassword("123");
        user.setNickName("oneao");
        System.out.println(JSONObject.toJSONString(user));
    }
}
