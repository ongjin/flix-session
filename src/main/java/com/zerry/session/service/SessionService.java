package com.zerry.session.service;

import java.util.List;

import com.zerry.session.model.SessionData;

public interface SessionService {
    SessionData createSession(SessionData sessionData);

    SessionData getSession(String sessionId);

    SessionData updateSession(SessionData sessionData);

    void deleteSession(String sessionId);

    List<SessionData> getAllSessions();

    void deleteAllSessions();
}
