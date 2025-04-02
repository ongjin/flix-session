package com.zerry.session.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerry.session.model.SessionEvent;
import com.zerry.session.model.SessionStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionWebSocketHandler extends TextWebSocketHandler {
    // userId를 키로 사용하여 WebSocket 세션을 저장
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = extractUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);
            log.info("WebSocket Connected - User: {}, Session: {}", userId, session.getId());

            // 연결 성공 메시지 전송
            sendMessage(session, new SessionEvent("connected", userId, "WebSocket connection established"));
        } else {
            log.warn("WebSocket connection attempt without userId - Session: {}", session.getId());
            session.close(CloseStatus.POLICY_VIOLATION);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = extractUserId(session);
        log.info("Received message from user: {} - Message: {}", userId, message.getPayload());

        try {
            SessionEvent event = objectMapper.readValue(message.getPayload(), SessionEvent.class);
            handleSessionEvent(userId, event);
        } catch (JsonProcessingException e) {
            log.error("Error processing message: {}", e.getMessage());
            sendError(session, "Invalid message format");
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String userId = extractUserId(session);
        log.error("Transport Error - User: {}, Session: {}", userId, session.getId(), exception);

        // 에러 발생 시 세션 정리
        cleanupSession(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = extractUserId(session);
        cleanupSession(userId, session);
        log.info("WebSocket Disconnected - User: {}, Status: {}", userId, status);
    }

    private void handleSessionEvent(String userId, SessionEvent event) {
        switch (event.getType()) {
            case "heartbeat":
                handleHeartbeat(userId);
                break;
            case "status_update":
                handleStatusUpdate(userId, event);
                break;
            default:
                log.warn("Unknown event type: {} from user: {}", event.getType(), userId);
        }
    }

    private void handleHeartbeat(String userId) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            sendMessage(session, new SessionEvent("heartbeat_ack", userId, "pong"));
        }
    }

    private void handleStatusUpdate(String userId, SessionEvent event) {
        // 상태 업데이트를 해당 사용자의 세션에만 전송
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            sendMessage(session, event);
        }
    }

    public void broadcastToUser(String userId, SessionEvent event) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            sendMessage(session, event);
        }
    }

    public void broadcastToAll(SessionEvent event) {
        String message;
        try {
            message = objectMapper.writeValueAsString(event);
            TextMessage textMessage = new TextMessage(message);

            userSessions.values().forEach(session -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                    }
                } catch (IOException e) {
                    log.error("Error broadcasting to session {}: {}", session.getId(), e.getMessage());
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Error serializing broadcast event: {}", e.getMessage());
        }
    }

    private void sendMessage(WebSocketSession session, SessionEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            log.error("Error sending message to session {}: {}", session.getId(), e.getMessage());
        }
    }

    private void sendError(WebSocketSession session, String errorMessage) {
        try {
            SessionEvent errorEvent = new SessionEvent("error", null, errorMessage);
            String message = objectMapper.writeValueAsString(errorEvent);
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            log.error("Error sending error message: {}", e.getMessage());
        }
    }

    private void cleanupSession(String userId, WebSocketSession session) {
        if (userId != null) {
            userSessions.remove(userId);
        }
        try {
            session.close(CloseStatus.NORMAL);
        } catch (IOException e) {
            log.error("Error closing session: {}", e.getMessage());
        }
    }

    private String extractUserId(WebSocketSession session) {
        // WebSocket 핸드셰이크 시 전달된 사용자 ID 추출
        // 실제 구현에서는 인증 토큰이나 세션 정보에서 추출
        return session.getUri().getQuery().split("userId=")[1];
    }
}
