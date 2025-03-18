package com.zerry.flix_session.service;

import java.util.List;

import com.zerry.flix_session.model.SessionData;

public interface SessionService {
    SessionData createSession(SessionData sessionData);

    SessionData getSession(String sessionId);

    SessionData updateSession(SessionData sessionData);

    void deleteSession(String sessionId);

    List<SessionData> getAllSessions();

    void deleteAllSessions();
}
