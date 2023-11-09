package cn.oneao.noteclient.utils.SendEmailUtils;

import cn.oneao.noteclient.pojo.entity.rabbitmq.RMCommentReplyMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class SendCommentReplyEmailUtil {
    @Value("${mail.fromMail.fromAddress}")
    private String fromEmail;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;
    public boolean sendEmailVerificationCode(RMCommentReplyMessage rmCommentReplyMessage) {
        Context context = new Context();
        context.setVariable("link",rmCommentReplyMessage.getLink());
        context.setVariable("comment",rmCommentReplyMessage.getComment());
        //3.指定邮件模板的位置
        //当前存放位置为resources包
        String emailContent = templateEngine.process("CommentReplyEmail.html", context);
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(rmCommentReplyMessage.getEmail());
            helper.setSubject("评论回复通知");
            helper.setText(emailContent, true);
            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
