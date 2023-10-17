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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SpringBootTest
@Slf4j
class NoteClientApplicationTests {
    @Test
    void contextLoads() throws ParseException {
        String s = "select id,user_id,small_note_title,small_note_tags,small_note_events,small_note_remark,is_top,is_finished,is_prompt,begin_time,end_time,create_time,update_time,is_delete from small_note where is_delete=0 and (user_id = 1) order by is_top desc,is_finished asc,create_time desc limit 8";
        Integer userId = getUserId(s);
        System.out.println(userId);
    }

    public Integer getUserId(String sqlStatement) {
        Integer userId = null;

        // 定义正则表达式
        String regex = "user_id\\s*=\\s*(\\d+)";

        // 创建Pattern对象
        Pattern pattern = Pattern.compile(regex);

        // 创建Matcher对象
        Matcher matcher = pattern.matcher(sqlStatement);

        // 查找匹配的字符串
        if (matcher.find()) {
            String userIdStr = matcher.group(1);
            userId = Integer.parseInt(userIdStr);
        }

        return userId;
    }
}
