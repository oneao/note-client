package cn.oneao.noteclient.server;

import cn.oneao.noteclient.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class DirectReceive {
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
}