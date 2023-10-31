package cn.oneao.noteclient.utils.SendEmailUtils;

import cn.oneao.noteclient.utils.RedisCache;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class SendRegisterEmailUtil {
    @Value("${mail.fromMail.fromAddress}")
    private String fromEmail;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisCache redisCache;
    public boolean sendEmailVerificationCode(String toEmail) {
        //1.生成随机验证码
        String code = getCode();
        //2.
        Context context = new Context();
        context.setVariable("verifyCode", Arrays.asList(code.split("")));
        //3.指定邮件模板的位置
        //当前存放位置为resources包
        String emailContent = templateEngine.process("email.html", context);
        MimeMessage message = mailSender.createMimeMessage();
        //4.
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("注册验证码");
            helper.setText(emailContent, true);
            mailSender.send(message);
            //设置过期时间为3分钟
            String redisKey = "USER_REGISTER_" + toEmail;
            redisCache.setCacheObject(redisKey,code,3,TimeUnit.MINUTES);
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCode() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            stringBuilder.append(random.nextInt(0, 10));
        }
        return stringBuilder.toString();
    }
}
