package com.zerry.session.websocket;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionWebSocketHandler extends TextWebSocketHandler {
    // 연결된 WebSocket 세션을 저장
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 연결이 성립되었을 때
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
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
        sessions.remove(session);
        log.info("WebSocket Disconnected: {} - {}", session.getId(), status);
    }

    /**
     * 세션 업데이트 시 모든 연결된 클라이언트에게 알림을 주고 싶다면,
     * 세션 WebSocketHandler 내부에서 WebSocketSession 목록을 관리하거나,
     * 별도의 SessionManager를 통해 broadcast할 수 있습니다.
     */
    public void broadcastSessionUpdate(String sessionId, String updatedInfo) {
        SessionEvent event = new SessionEvent("update", sessionId, updatedInfo);
        try {
            String message = objectMapper.writeValueAsString(event);
            broadcast(message);
        } catch (JsonProcessingException e) {
            log.error("Error serializing session event", e);
        }
    }

    // 실시간 알림을 모든 연결된 클라이언트에게 브로드캐스트
    public void broadcast(String message) {
        TextMessage textMessage = new TextMessage(message);
        sessions.forEach(s -> {
            try {
                s.sendMessage(textMessage);
            } catch (Exception e) {
                log.error("Error sending message to session {}: {}", s.getId(), e.getMessage());
            }
        });
    }
}
