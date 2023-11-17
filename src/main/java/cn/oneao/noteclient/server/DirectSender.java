package cn.oneao.noteclient.server;

import cn.oneao.noteclient.config.RabbitMqConfig;
import cn.oneao.noteclient.pojo.entity.rabbitmq.RMCommentReplyMessage;
import cn.oneao.noteclient.pojo.entity.rabbitmq.RMCommentReplyNotice;
import cn.oneao.noteclient.pojo.entity.rabbitmq.RMUserLevelUpNotice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DirectSender {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 向RabbitMQ队列中发送消息，方便后面客户端可以从队列中读取该消息
     */
    public void sendDirect(String msg) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE, RabbitMqConfig.ROUTING_KEY, msg);
    }

    public void sendCommentReplyMessage(RMCommentReplyMessage rmCommentReplyMessage){
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE, RabbitMqConfig.COMMENT_REPLY_KEY, rmCommentReplyMessage);
    }

    public void sendCommentReplyNotice(RMCommentReplyNotice rmCommentReplyNotice){
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE, RabbitMqConfig.COMMENT_REPLY_NOTICE_KEY, rmCommentReplyNotice);
    }
    public void sendUserLevelUpNotice(RMUserLevelUpNotice rmUserLevelUpNotice){
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE,RabbitMqConfig.USER_LEVEL_UP_KEY,rmUserLevelUpNotice);
    }
}