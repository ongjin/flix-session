package com.zerry.session.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.zerry.session.model.SessionData;
import com.zerry.session.model.SessionStatus;

@Repository
public interface SessionRepository {
    void saveSession(SessionData sessionData);

    Optional<SessionData> findBySessionId(String sessionId);

    List<SessionData> findByUserId(String userId);

    List<SessionData> findByStatus(SessionStatus status);

    void deleteSession(String sessionId);

    void updateSessionTTL(String sessionId, long ttlSeconds);
}
