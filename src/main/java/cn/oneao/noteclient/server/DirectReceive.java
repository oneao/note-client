package cn.oneao.noteclient.server;

import cn.oneao.noteclient.config.RabbitMqConfig;
import cn.oneao.noteclient.pojo.entity.rabbitmq.RMCommentReplyMessage;
import cn.oneao.noteclient.pojo.entity.rabbitmq.RMCommentReplyNotice;
import cn.oneao.noteclient.utils.SendEmailUtils.SendCommentReplyEmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class DirectReceive {
    @Autowired
    private SendCommentReplyEmailUtil sendCommentReplyEmailUtil;

    /**
     * 监听客户端发送到RabbitMQ队列中的消息，并把消息发送给WebSocketServer
     *
     * @param msg
     * @throws InterruptedException
     * @throws IOException
     */
    @RabbitListener(queues = RabbitMqConfig.DIRECT_QUEUE)
    @RabbitHandler
    public void processToPre(String msg) throws InterruptedException, IOException {
        Thread.sleep(500);
        WebSocketServer.sendInfo(msg);
    }

    @RabbitListener(queues = RabbitMqConfig.COMMENT_REPLY_QUEUE)
    @RabbitHandler
    public void receiveCommentReplyMessage(RMCommentReplyMessage rmCommentReplyMessage) throws InterruptedException {
        log.info("RMCommentReplyMessage:{}",rmCommentReplyMessage);
        Thread.sleep(500);
        sendCommentReplyEmailUtil.sendEmailVerificationCode(rmCommentReplyMessage);
    }

    @RabbitListener(queues = RabbitMqConfig.COMMENT_REPLY_NOTICE_QUEUE)
    @RabbitHandler
    public void receiveCommentReplyNotice(RMCommentReplyNotice rmCommentReplyNotice) throws InterruptedException {
        Thread.sleep(500);
        WebSocketServer.sendObjMessage(rmCommentReplyNotice.getUserId(),rmCommentReplyNotice);
    }
}