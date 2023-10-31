package cn.oneao.noteclient.server;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.WebSocket;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/webSocket")
@Component
@Slf4j
public class WebSocketServer {

    /**
     * 存放每个客户端对应的WebSocket对象
     */
    public static CopyOnWriteArraySet<WebSocketServer> webSockets = new CopyOnWriteArraySet<WebSocketServer>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;


    @Autowired
    static DirectSender directSender;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) throws InterruptedException, IOException {
        this.session = session;
        webSockets.add(this);
    }

    @OnClose
    public void onClose() throws IOException {
        webSockets.remove(this);
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String msg) throws InterruptedException {
        // 将消息发送给前端
        for (WebSocketServer webSocketServer : webSockets) {
            try {
                log.info("WebSocketServer");
                webSocketServer.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }

    /**
     * 发送信息
     *
     * @param message 消息
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 自定义消息推送、可群发、单发
     *
     * @param message 消息
     */
    public static void sendInfo(String message) throws IOException {
        for (WebSocketServer item : webSockets) {
            item.sendMessage(message);
        }
    }

}
