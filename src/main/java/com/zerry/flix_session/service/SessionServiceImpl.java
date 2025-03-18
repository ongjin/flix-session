package com.zerry.flix_session.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zerry.flix_session.model.SessionData;
import com.zerry.flix_session.repository.SessionDataRepository;
import com.zerry.flix_session.repository.SessionRepository;
import com.zerry.flix_session.websocket.SessionWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    @Autowired
    private SessionDataRepository sessionDataRepository;

    private final SessionWebSocketHandler webSocketHandler;

    @Override
    public SessionData createSession(SessionData sessionData) {
        // 새로운 세션 ID 생성
        sessionData.setSessionId(UUID.randomUUID().toString());
        sessionData.setCreatedAt(Instant.now());
        sessionData.setUpdatedAt(Instant.now());
        if (sessionData.getStatus() == null) {
            sessionData.setStatus("active");
        }
        webSocketHandler.broadcast("세션 생성: " + sessionData.getSessionId());
        return sessionDataRepository.save(sessionData);
    }

    /**
     * 전체 세션 조회
     */
    @Override
    public List<SessionData> getAllSessions() {
        List<SessionData> sessions = new ArrayList<>();
        sessionDataRepository.findAll().forEach(sessions::add);
        return sessions;
    }

    /**
     * 전체 세션 삭제
     */
    @Override
    public void deleteAllSessions() {
        sessionDataRepository.deleteAll();
    }

    @Override
    public SessionData getSession(String sessionId) {
        Optional<SessionData> optional = sessionDataRepository.findById(sessionId);
        return optional.orElse(null);
    }

    @Override
    public SessionData updateSession(SessionData sessionData) {
        sessionData.setUpdatedAt(Instant.now());
        webSocketHandler.broadcastSessionUpdate(sessionData.getSessionId(), "세션 업데이트 완료");
        return sessionDataRepository.save(sessionData);
    }

    @Override
    public void deleteSession(String sessionId) {
        sessionDataRepository.deleteById(sessionId);
        webSocketHandler.broadcastSessionUpdate(sessionId, "세션 삭제 완료");
    }

}
