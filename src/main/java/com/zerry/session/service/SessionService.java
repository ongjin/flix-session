package com.zerry.session.service;

import java.util.List;

import com.zerry.session.dto.SessionData;
import com.zerry.session.model.SessionStatus;

public interface SessionService {
    SessionData createSession(String userId, String deviceInfo, String ipAddress);

    SessionData createSession(SessionData sessionData);

    SessionData getSession(String sessionId);

    void refreshSession(String sessionId);

    void deleteSession(String sessionId);

    List<SessionData> getUserSessions(String userId);

    List<SessionData> getActiveSessions();

    void updateSessionStatus(String sessionId, SessionStatus status);

    List<SessionData> getAllSessions();

    SessionData updateSession(SessionData sessionData);

    void deleteAllSessions();
}
