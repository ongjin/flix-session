package com.zerry.session.service;

import java.util.List;

import com.zerry.session.dto.SessionData;
import com.zerry.session.model.SessionStatus;

public interface SessionService {
    SessionData createSession(SessionData sessionData);

    SessionData getSession(String sessionId);

    SessionData refreshSession(String sessionId);

    SessionData deleteSession(String sessionId);

    List<SessionData> getUserSessions(String userId);

    List<SessionData> getActiveSessions();

    SessionData updateSessionStatus(String sessionId, SessionStatus status);

    List<SessionData> getAllSessions();

    SessionData updateSession(SessionData sessionData);

    List<SessionData> deleteAllSessions();
}