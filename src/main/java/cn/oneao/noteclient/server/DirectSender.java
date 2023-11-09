package cn.oneao.noteclient.server;

import cn.oneao.noteclient.config.RabbitMQConfig;
import cn.oneao.noteclient.pojo.entity.rabbitmq.RMCommentReplyMessage;
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
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.ROUTING_KEY, msg);
    }
    //邮箱，链接，评论
    public void sendCommentReplyMessage(RMCommentReplyMessage rmCommentReplyMessage){
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE,RabbitMQConfig.COMMENT_REPLY_KEY,rmCommentReplyMessage);
    }
}