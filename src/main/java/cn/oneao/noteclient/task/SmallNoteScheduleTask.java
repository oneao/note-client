package cn.oneao.noteclient.task;

import cn.oneao.noteclient.annotations.LogSqlExecution;
import cn.oneao.noteclient.pojo.entity.SmallNote;
import cn.oneao.noteclient.pojo.entity.User;
import cn.oneao.noteclient.service.SmallNoteService;
import cn.oneao.noteclient.service.UserService;
import cn.oneao.noteclient.utils.GlobalObjectUtils.GlobalObject;
import cn.oneao.noteclient.utils.GlobalObjectUtils.GlobalObjectUtil;
import cn.oneao.noteclient.utils.TimeUtils;
import cn.oneao.noteclient.utils.SendEmailUtils.SendExpirationNoticeEmailUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SmallNoteScheduleTask {
    @Autowired
    private SmallNoteService smallNoteService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SendExpirationNoticeEmailUtil sendExpirationNoticeEmailUtil;

    //定义定时任务
    //每八分钟执行一次
    @Scheduled(cron = "0 0/8 * * * ?")
    @LogSqlExecution
    public void configureTasks() {
        //移除后重新创建
        GlobalObject globalObject = GlobalObjectUtil.getInstance().getObject();
        globalObject.setUserId(0);
        LambdaQueryWrapper<SmallNote> queryWrapper = new LambdaQueryWrapper<>();
        //开启消息通知
        queryWrapper.eq(SmallNote::getIsPrompt, 1);
        queryWrapper.eq(SmallNote::getIsFinished, 0);
        List<SmallNote> list = smallNoteService.list(queryWrapper);
        //获取到小记列表
        if (list.isEmpty()) {
            return;
        }
        //遍历循环
        for (SmallNote smallNote : list) {
            Integer userId = smallNote.getUserId();
            Date endTime = smallNote.getEndTime();
            Date beginTime = smallNote.getBeginTime();
            Date nowTime = new Date();
            //检查现在时间和结束时间的差值是不是在 11h55m - 12h05m
            boolean flag = TimeUtils.isWithinRange(endTime, nowTime);
            if (flag) {
                //如果为true，则发通知
                User user = userService.getById(userId);
                String userEmail = user.getEmail();
                String smallNoteTitle = smallNote.getSmallNoteTitle();
                smallNoteTitle = "《" + smallNoteTitle + "》";
                String redis_key = userEmail + ":" + smallNoteTitle + "+" + beginTime.toString();
                if (Boolean.FALSE.equals(redisTemplate.hasKey(redis_key))) {
                    //如果没有这个key的话，则代表没有发送通知。如果有的话，则代表已经发送过通知了。
                    redisTemplate.opsForValue().set(redis_key, redis_key, 10, TimeUnit.MINUTES);
                    sendExpirationNoticeEmailUtil.sendEmailVerificationCode(userEmail, smallNoteTitle);
                } else {
                    return;
                }
            }
        }
    }
}
