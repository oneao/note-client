package cn.oneao.noteclient;

import cn.oneao.noteclient.constant.FileConstants;
import cn.oneao.noteclient.pojo.entity.SmallNote;
import cn.oneao.noteclient.pojo.entity.User;
import cn.oneao.noteclient.pojo.vo.UserLoginVO;
import cn.oneao.noteclient.service.SmallNoteService;
import cn.oneao.noteclient.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SpringBootTest
@Slf4j
class NoteClientApplicationTests {
    @Test
    void contextLoads() {
        List<List<String>> list = new ArrayList<>();
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        list1.add("a");
        list1.add("b");
        list1.add("c");
        list2.add("a");
        list2.add("b");
        list.add(list1);
        list.add(list2);
        String string = list.toString();
        System.out.println(string);
    }
}
