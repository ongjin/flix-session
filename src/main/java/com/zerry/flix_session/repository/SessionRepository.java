package com.zerry.flix_session.repository;

import com.zerry.flix_session.model.SessionData;

public interface SessionRepository {
    void saveSession(SessionData sessionData);

    SessionData findSessionById(String sessionId);

    void deleteSession(String sessionId);

    void updateSessionTTL(String sessionId, long ttlSeconds);
}
