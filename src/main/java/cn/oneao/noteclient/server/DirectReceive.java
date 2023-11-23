package cn.oneao.noteclient.server;

import cn.oneao.noteclient.config.RabbitMqConfig;
import cn.oneao.noteclient.constant.DocumentIndexConstant;
import cn.oneao.noteclient.constant.MqConstants;
import cn.oneao.noteclient.pojo.entity.es.ESNote;
import cn.oneao.noteclient.pojo.entity.rabbitmq.RMCommentReplyMessage;
import cn.oneao.noteclient.pojo.entity.rabbitmq.RMCommentReplyNotice;
import cn.oneao.noteclient.pojo.entity.rabbitmq.RMUserLevelUpNotice;
import cn.oneao.noteclient.utils.ElasticSearchUtil;
import cn.oneao.noteclient.utils.SendEmailUtils.SendCommentReplyEmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;

@Component
@Slf4j
public class DirectReceive {
    @Autowired
    private SendCommentReplyEmailUtil sendCommentReplyEmailUtil;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

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

    //发送邮件通知评论被回复了
    @RabbitListener(queues = RabbitMqConfig.COMMENT_REPLY_QUEUE)
    @RabbitHandler
    public void receiveCommentReplyMessage(RMCommentReplyMessage rmCommentReplyMessage) throws InterruptedException {
        Thread.sleep(500);
        sendCommentReplyEmailUtil.sendEmailVerificationCode(rmCommentReplyMessage);
    }

    //站内通知
    @RabbitListener(queues = RabbitMqConfig.COMMENT_REPLY_NOTICE_QUEUE)
    @RabbitHandler
    public void receiveCommentReplyNotice(RMCommentReplyNotice rmCommentReplyNotice) throws InterruptedException {
        Thread.sleep(500);
        WebSocketServer.sendObjMessage(rmCommentReplyNotice.getUserId(), rmCommentReplyNotice);
    }

    //站内通知
    @RabbitListener(queues = RabbitMqConfig.USER_LEVEL_UP_QUEUE)
    @RabbitHandler
    public void receiveUserLevelUpNotice(RMUserLevelUpNotice rmUserLevelUpNotice) throws InterruptedException {
        Thread.sleep(500);
        WebSocketServer.sendObjMessage(rmUserLevelUpNotice.getUserId(), rmUserLevelUpNotice);
    }
    //es文档操作，插入或更新文档
    @RabbitListener(queues = MqConstants.NOTE_INSERT_OR_UPDATE_QUEUE)
    @RabbitHandler
    public void receiveInsertOrUpdateNoteDocument(ESNote esNote) {
        if (ObjectUtils.isEmpty(esNote)) {
            return;
        } else {
            String id = esNote.getId();
            boolean flag = elasticSearchUtil.insertOrUpdateDocument(DocumentIndexConstant.NOTE_INDEX, esNote, id);
        }
    }
    //es文档操作，删除文档
    @RabbitListener(queues = MqConstants.NOTE_DELETE_QUEUE)
    @RabbitHandler
    public void receiveDeleteNoteDocument(String id) {
        if (ObjectUtils.isEmpty(id)) {
            return;
        } else {
            elasticSearchUtil.deleteDocument(DocumentIndexConstant.NOTE_INDEX,id);
        }
    }
}