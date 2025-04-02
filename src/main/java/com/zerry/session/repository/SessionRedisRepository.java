package com.zerry.session.repository;

import com.zerry.session.model.SessionData;

public interface SessionRedisRepository {
    void saveSession(SessionData sessionData);

    SessionData findSessionById(String sessionId);

    void deleteSession(String sessionId);

    void updateSessionTTL(String sessionId, long ttlSeconds);
}