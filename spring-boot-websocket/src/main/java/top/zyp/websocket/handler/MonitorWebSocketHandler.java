package top.zyp.websocket.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
public class MonitorWebSocketHandler extends TextWebSocketHandler {

    public static final CopyOnWriteArraySet<WebSocketSession> SESSIONS = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        SESSIONS.add(session);
        log.info("[WS-监控] 连接建立，当前在线：{}", SESSIONS.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        SESSIONS.remove(session);
        log.info("[WS-监控] 连接关闭，当前在线：{}", SESSIONS.size());
    }

    /**
     * 广播监控消息
     */
    public void broadcast(String message) {
        for (WebSocketSession session : SESSIONS) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.error("[WS-监控] 推送失败", e);
                }
            }
        }
    }
}