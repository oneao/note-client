package cn.oneao.noteclient.server;

import cn.oneao.noteclient.config.RabbitMQConfig;
import cn.oneao.noteclient.pojo.entity.rabbitmq.RMCommentReplyMessage;
import cn.oneao.noteclient.utils.SendEmailUtils.SendCommentReplyEmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class DirectReceive {
    @Autowired
    private SendCommentReplyEmailUtil sendCommentReplyEmailUtil;
    /**
     * 监听客户端发送到RabbitMQ队列中的消息，并把消息发送给WebSocketServer
     * @param msg
     * @throws InterruptedException
     * @throws IOException
     */
    @RabbitListener(queues = RabbitMQConfig.DIRECT_QUEUE)
    @RabbitHandler
    public void processToPre(String msg) throws InterruptedException, IOException {
        Thread.sleep(500);
        WebSocketServer.sendInfo(msg);
    }

    @RabbitListener(queues = RabbitMQConfig.COMMENT_REPLY_QUEUE)
    @RabbitHandler
    public void receiveCommentReplyMessage(RMCommentReplyMessage rmCommentReplyMessage) {
        sendCommentReplyEmailUtil.sendEmailVerificationCode(rmCommentReplyMessage);
    }
}