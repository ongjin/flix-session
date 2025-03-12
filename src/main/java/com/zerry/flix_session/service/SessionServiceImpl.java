package com.zerry.flix_session.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.zerry.flix_session.dto.PatchRequest;
import com.zerry.flix_session.kafka.SessionEventProducer;
import com.zerry.flix_session.model.SessionData;
import com.zerry.flix_session.repository.SessionRepository;
import com.zerry.flix_session.response.ApiResponse;
import com.zerry.flix_session.websocket.SessionWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    private final SessionEventProducer sessionEventProducer; // Kafka 사용 (옵션)

    private final SessionWebSocketHandler webSocketHandler;

    private static final long DEFAULT_SESSION_TTL = 3600; // 1시간

    @Override
    public SessionData createSession(SessionData sessionData) {
        sessionRepository.saveSession(sessionData);
        // Kafka 이벤트 발행 (옵션)
        sessionEventProducer.sendSessionEvent("create", sessionData);
        return sessionData;
    }

    @Override
    public SessionData getSession(String sessionId) {
        return sessionRepository.findSessionById(sessionId);
    }

    @Override
    public SessionData updateSession(SessionData sessionData) {
        sessionRepository.saveSession(sessionData);
        // Kafka 이벤트 발행 (옵션)
        sessionEventProducer.sendSessionEvent("update", sessionData);

        renewSession(sessionData.getSessionId());
        return sessionData;
    }

    @Override
    public void deleteSession(String sessionId) {
        sessionRepository.deleteSession(sessionId);
        // Kafka 이벤트 발행 (옵션)
        sessionEventProducer.sendSessionEvent("delete", new SessionData(sessionId, null, null, null, 0, null, null));
    }

    @Override
    public void renewSession(String sessionId) {
        // 특정 이벤트 발생 시 세션 TTL만 연장
        sessionRepository.updateSessionTTL(sessionId, DEFAULT_SESSION_TTL);
    }
}
