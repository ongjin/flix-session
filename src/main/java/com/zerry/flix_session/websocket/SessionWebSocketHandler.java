package com.zerry.flix_session.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionWebSocketHandler extends TextWebSocketHandler {
    // 연결이 성립되었을 때
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket Connected: {}", session.getId());
    }

    // 클라이언트로부터 메시지를 받았을 때
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("Received message: {} from session: {}", message.getPayload(), session.getId());
        // 예: echo나 특정 로직 수행
        session.sendMessage(new TextMessage("ACK: " + message.getPayload()));
    }

    // 에러 발생 시
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Transport Error in session: {}", session.getId(), exception);
    }

    // 연결이 종료되었을 때
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket Disconnected: {} - {}", session.getId(), status);
    }

    /**
     * 세션 업데이트 시 모든 연결된 클라이언트에게 알림을 주고 싶다면,
     * 세션 WebSocketHandler 내부에서 WebSocketSession 목록을 관리하거나,
     * 별도의 SessionManager를 통해 broadcast할 수 있습니다.
     */
    public void broadcastSessionUpdate(String sessionId, String updatedInfo) {
        // connectedSessions.forEach(s -> s.sendMessage(...));
    }
}
