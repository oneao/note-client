package cn.oneao.noteclient;

import cn.oneao.noteclient.utils.HttpClientUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;


@SpringBootTest(classes = NoteClientApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class NoteClientApplicationTests {
    @Test
    void contextLoads() {
        String value = HttpClientUtil.doGet("https://ip.taobao.com/outGetIpInfo?ip=117.158.200.38&accessKey=alibaba-inc", true);
        JSONObject jsonObject = JSONObject.parseObject(value);
        System.out.println(value);
        System.out.println(jsonObject.getJSONObject("data").get("country"));
        System.out.println(jsonObject.getJSONObject("data").get("region"));
        System.out.println(jsonObject.getJSONObject("data").get("city"));
    }
}
