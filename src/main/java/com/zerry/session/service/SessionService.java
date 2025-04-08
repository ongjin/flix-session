package com.zerry.session.service;

import java.util.List;
import java.util.Optional;

import com.zerry.session.dto.SessionData;
import com.zerry.session.model.SessionStatus;

public interface SessionService {
    SessionData createSession(SessionData sessionData);

    SessionData getSession(String sessionId);

    SessionData refreshSession(String sessionId);

    Optional<SessionData> deleteSession(String sessionId);

    List<SessionData> getUserSessions(Long userId);

    List<SessionData> getActiveSessions();

    SessionData updateSessionStatus(String sessionId, SessionStatus status);

    List<SessionData> getAllSessions();

    SessionData updateSession(SessionData sessionData);

    List<SessionData> deleteAllSessions();

    /**
     * 사용자 ID와 비디오 ID로 세션을 검색합니다.
     * 
     * @param userId  사용자 ID
     * @param videoId 비디오 ID
     * @return 검색된 세션 또는 null
     */
    SessionData findSessionByUserAndVideo(Long userId, String videoId);
}