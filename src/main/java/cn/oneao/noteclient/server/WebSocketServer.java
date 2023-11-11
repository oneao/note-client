package cn.oneao.noteclient.server;

import cn.oneao.noteclient.utils.GlobalObjectUtils.UserContext;
import com.alibaba.fastjson2.JSONObject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint(value = "/webSocket/{userId}")
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
    private static Map<Integer, Session> sessionPool = new ConcurrentHashMap<Integer, Session>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("userId") Integer userId) throws InterruptedException, IOException {
        this.session = session;
        webSockets.add(this);
        if (userId != -1) {
            if (!sessionPool.containsKey(userId)) {
                sessionPool.put(userId, session);
            }
        }
    }

    @OnClose
    public void onClose() throws IOException {
        webSockets.remove(this);
        if (session != null) {
            try {
                Iterator<Map.Entry<Integer, Session>> iterator = sessionPool.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, Session> entry = iterator.next();
                    if (entry.getValue().equals(session)) {
                        iterator.remove();
                        break;
                    }
                }
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
    public synchronized void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
    /**
     * 群发
     * @param message 消息
     */
    public static void sendInfo(String message) throws IOException {
        for (WebSocketServer item : webSockets) {
            item.sendMessage(message);
        }
    }
    // 此为单点消息 (发送对象)
    public static void sendObjMessage(Integer userId, Object message) {
        Session session = sessionPool.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(JSONObject.toJSONString(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 处理会话已关闭的情况
        }
    }

}
