package com.zerry.flix_session.service;

import org.springframework.stereotype.Service;

import com.zerry.flix_session.model.SessionData;
import com.zerry.flix_session.repository.SessionRepository;
import com.zerry.flix_session.websocket.SessionWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;

    private final SessionWebSocketHandler webSocketHandler;

    private static final long DEFAULT_SESSION_TTL = 3600; // 1시간

    @Override
    public SessionData createSession(SessionData sessionData) {
        sessionRepository.saveSession(sessionData);
        webSocketHandler.broadcast("세션 생성: " + sessionData.getSessionId());
        return sessionData;
    }

    @Override
    public SessionData getSession(String sessionId) {
        return sessionRepository.findSessionById(sessionId);
    }

    @Override
    public SessionData updateSession(SessionData sessionData) {
        sessionRepository.saveSession(sessionData);
        webSocketHandler.broadcastSessionUpdate(sessionData.getSessionId(), "세션 업데이트 완료");
        renewSession(sessionData.getSessionId());
        return sessionData;
    }

    @Override
    public void deleteSession(String sessionId) {
        sessionRepository.deleteSession(sessionId);
        webSocketHandler.broadcastSessionUpdate(sessionId, "세션 삭제 완료");
    }

    @Override
    public void renewSession(String sessionId) {
        // 특정 이벤트 발생 시 세션 TTL만 연장
        sessionRepository.updateSessionTTL(sessionId, DEFAULT_SESSION_TTL);
    }
}
