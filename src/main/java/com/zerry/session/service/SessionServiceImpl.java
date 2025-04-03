package com.zerry.session.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.zerry.session.dto.SessionData;
import com.zerry.session.model.SessionStatus;
import com.zerry.session.model.SessionType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SessionServiceImpl implements SessionService {
    private final Map<String, SessionData> sessions = new ConcurrentHashMap<>();

    @Override
    public SessionData createSession(SessionData sessionData) {
        if (sessionData == null) {
            throw new IllegalArgumentException("SessionData cannot be null");
        }

        // 필수 필드 검증
        if (sessionData.getUserId() == null || sessionData.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("userId is required");
        }

        // sessionId가 없는 경우 생성
        if (sessionData.getSessionId() == null || sessionData.getSessionId().trim().isEmpty()) {
            sessionData.setSessionId(UUID.randomUUID().toString());
        }

        // 기본값 설정
        if (sessionData.getLastAccessTime() == null) {
            sessionData.setLastAccessTime(LocalDateTime.now());
        }
        if (sessionData.getStatus() == null) {
            sessionData.setStatus(SessionStatus.ACTIVE);
        }
        if (sessionData.getType() == null) {
            sessionData.setType(determineSessionType(sessionData.getDeviceInfo()));
        }

        sessions.put(sessionData.getSessionId(), sessionData);
        return sessionData;
    }

    private SessionType determineSessionType(String deviceInfo) {
        if (deviceInfo == null) {
            return SessionType.OTHER;
        }

        String lowerDeviceInfo = deviceInfo.toLowerCase();
        if (lowerDeviceInfo.contains("mobile") || lowerDeviceInfo.contains("android")
                || lowerDeviceInfo.contains("ios")) {
            return SessionType.MOBILE;
        } else if (lowerDeviceInfo.contains("tv") || lowerDeviceInfo.contains("smart-tv")) {
            return SessionType.TV;
        } else {
            return SessionType.WEB;
        }
    }

    @Override
    public SessionData getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public SessionData refreshSession(String sessionId) {
        SessionData session = sessions.get(sessionId);
        if (session != null) {
            session.setLastAccessTime(LocalDateTime.now());
            return session;
        }
        return null;
    }

    @Override
    public SessionData deleteSession(String sessionId) {
        return sessions.remove(sessionId);
    }

    @Override
    public List<SessionData> getUserSessions(String userId) {
        return sessions.values().stream()
                .filter(session -> session.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<SessionData> getActiveSessions() {
        return sessions.values().stream()
                .filter(session -> session.getStatus() == SessionStatus.ACTIVE)
                .toList();
    }

    @Override
    public SessionData updateSessionStatus(String sessionId, SessionStatus status) {
        SessionData session = sessions.get(sessionId);
        if (session != null) {
            session.setStatus(status);
            return session;
        }
        return null;
    }

    @Override
    public List<SessionData> getAllSessions() {
        return sessions.values().stream().toList();
    }

    @Override
    public SessionData updateSession(SessionData sessionData) {
        sessions.put(sessionData.getSessionId(), sessionData);
        return sessionData;
    }

    @Override
    public List<SessionData> deleteAllSessions() {
        List<SessionData> deletedSessions = sessions.values().stream().toList();
        sessions.clear();
        return deletedSessions;
    }
}