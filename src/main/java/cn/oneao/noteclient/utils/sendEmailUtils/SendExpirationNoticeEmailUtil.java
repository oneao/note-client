package cn.oneao.noteclient.utils.sendEmailUtils;

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

import java.util.Random;

@Component
public class SendExpirationNoticeEmailUtil {
    @Value("${mail.fromMail.fromAddress}")
    private String fromEmail;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;

    public boolean sendEmailVerificationCode(String toEmail,String smallNoteTitle) {
        Context context = new Context();
        context.setVariable("smallNoteTitle",smallNoteTitle);
        //当前存放位置为resources包
        String emailContent = templateEngine.process("expirationNotice.html", context);
        MimeMessage message = mailSender.createMimeMessage();
        //4.
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("到期通知");
            helper.setText(emailContent, true);
            mailSender.send(message);
            //设置过期时间为3分钟
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
