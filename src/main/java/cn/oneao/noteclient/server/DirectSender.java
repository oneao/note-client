package cn.oneao.noteclient.server;

import cn.oneao.noteclient.config.RabbitMqConfig;
import cn.oneao.noteclient.constant.MqConstants;
import cn.oneao.noteclient.pojo.entity.es.ESNote;
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

    //es类的文档操作
    //新增或更新
    public void sendDocumentInsertOrUpdateNotice(ESNote esNote){
        log.info("==============1===================");
        rabbitTemplate.convertAndSend(MqConstants.NOTE_EXCHANGE,MqConstants.NOTE_INSERT_OR_UPDATE_KEY,esNote);
    }
    //删除
    public void sendDocumentDeleteNotice(String id){
        rabbitTemplate.convertAndSend(MqConstants.NOTE_EXCHANGE,MqConstants.NOTE_DELETE_KEY,id);
    }
}