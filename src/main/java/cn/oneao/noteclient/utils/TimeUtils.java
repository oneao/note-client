package cn.oneao.noteclient.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
@Slf4j
public class TimeUtils {
    public static boolean isWithinRange(Date endTime, Date nowTime) {
        LocalDateTime endTimeLocal = endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime nowTimeLocal = nowTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Duration duration = Duration.between(nowTimeLocal, endTimeLocal);

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        log.info("相差时间小时:{}",hours);
        log.info("相差时间分钟:{}",minutes);
        return (minutes >= 715 && minutes <= 725) || (hours == 12 && minutes <= 5);
    }
}
