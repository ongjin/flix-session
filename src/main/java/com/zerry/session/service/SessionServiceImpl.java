package com.zerry.session.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zerry.session.dto.SessionData;
import com.zerry.session.model.Session;
import com.zerry.session.model.SessionStatus;
import com.zerry.session.model.SessionType;
import com.zerry.session.repository.SessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final RedisTemplate<String, Session> redisTemplate;
    private static final long SESSION_TTL_HOURS = 24;

    @Override
    @Transactional
    public SessionData createSession(String userId, String deviceInfo, String ipAddress) {
        String sessionId = UUID.randomUUID().toString();

        Session session = Session.builder()
                .sessionId(sessionId)
                .userId(userId)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .lastAccessTime(LocalDateTime.now())
                .expiryTime(LocalDateTime.now().plusHours(SESSION_TTL_HOURS))
                .status(SessionStatus.ACTIVE)
                .type(determineSessionType(deviceInfo))
                .build();

        redisTemplate.opsForValue().set(
                "session:" + sessionId,
                session,
                SESSION_TTL_HOURS,
                TimeUnit.HOURS);

        return convertToSessionData(session);
    }

    @Override
    @Transactional
    public SessionData createSession(SessionData sessionData) {
        if (sessionData.getSessionId() == null) {
            sessionData.setSessionId(UUID.randomUUID().toString());
        }
        if (sessionData.getLastAccessTime() == null) {
            sessionData.setLastAccessTime(LocalDateTime.now());
        }
        if (sessionData.getExpiryTime() == null) {
            sessionData.setExpiryTime(LocalDateTime.now().plusHours(SESSION_TTL_HOURS));
        }
        if (sessionData.getStatus() == null) {
            sessionData.setStatus(SessionStatus.ACTIVE);
        }
        if (sessionData.getType() == null) {
            sessionData.setType(determineSessionType(sessionData.getDeviceInfo()));
        }

        Session session = convertToSession(sessionData);
        redisTemplate.opsForValue().set(
                "session:" + session.getSessionId(),
                session,
                SESSION_TTL_HOURS,
                TimeUnit.HOURS);

        return convertToSessionData(session);
    }

    @Override
    public SessionData getSession(String sessionId) {
        Session session = redisTemplate.opsForValue().get("session:" + sessionId);
        if (session == null) {
            throw new RuntimeException("세션을 찾을 수 없습니다: " + sessionId);
        }
        return convertToSessionData(session);
    }

    @Override
    @Transactional
    public void refreshSession(String sessionId) {
        Session session = convertToSession(getSession(sessionId));
        session.setLastAccessTime(LocalDateTime.now());
        session.setExpiryTime(LocalDateTime.now().plusHours(SESSION_TTL_HOURS));

        redisTemplate.opsForValue().set(
                "session:" + sessionId,
                session,
                SESSION_TTL_HOURS,
                TimeUnit.HOURS);
    }

    @Override
    @Transactional
    public void deleteSession(String sessionId) {
        redisTemplate.delete("session:" + sessionId);
    }

    @Override
    public List<SessionData> getUserSessions(String userId) {
        return redisTemplate.keys("session:*")
                .stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .filter(session -> session != null && session.getUserId().equals(userId))
                .map(this::convertToSessionData)
                .collect(Collectors.toList());
    }

    @Override
    public List<SessionData> getActiveSessions() {
        return redisTemplate.keys("session:*")
                .stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .filter(session -> session != null && session.getStatus() == SessionStatus.ACTIVE)
                .map(this::convertToSessionData)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateSessionStatus(String sessionId, SessionStatus status) {
        Session session = convertToSession(getSession(sessionId));
        session.setStatus(status);

        redisTemplate.opsForValue().set(
                "session:" + sessionId,
                session,
                SESSION_TTL_HOURS,
                TimeUnit.HOURS);
    }

    @Override
    public List<SessionData> getAllSessions() {
        return redisTemplate.keys("session:*")
                .stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .filter(session -> session != null)
                .map(this::convertToSessionData)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SessionData updateSession(SessionData sessionData) {
        Session session = convertToSession(sessionData);
        redisTemplate.opsForValue().set(
                "session:" + session.getSessionId(),
                session,
                SESSION_TTL_HOURS,
                TimeUnit.HOURS);

        return convertToSessionData(session);
    }

    @Override
    @Transactional
    public void deleteAllSessions() {
        redisTemplate.keys("session:*").forEach(redisTemplate::delete);
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

    private SessionData convertToSessionData(Session session) {
        return SessionData.builder()
                .sessionId(session.getSessionId())
                .userId(session.getUserId())
                .deviceInfo(session.getDeviceInfo())
                .ipAddress(session.getIpAddress())
                .lastAccessTime(session.getLastAccessTime())
                .expiryTime(session.getExpiryTime())
                .status(session.getStatus())
                .type(session.getType())
                .build();
    }

    private Session convertToSession(SessionData sessionData) {
        return Session.builder()
                .sessionId(sessionData.getSessionId())
                .userId(sessionData.getUserId())
                .deviceInfo(sessionData.getDeviceInfo())
                .ipAddress(sessionData.getIpAddress())
                .lastAccessTime(sessionData.getLastAccessTime())
                .expiryTime(sessionData.getExpiryTime())
                .status(sessionData.getStatus())
                .type(sessionData.getType())
                .build();
    }
}