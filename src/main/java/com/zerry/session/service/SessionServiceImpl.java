package com.zerry.session.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.zerry.session.dto.SessionData;
import com.zerry.session.model.SessionStatus;
import com.zerry.session.model.SessionType;
import com.zerry.session.repository.SessionRedisRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRedisRepository sessionRepository;

    @Override
    public SessionData createSession(SessionData sessionData) {
        if (sessionData == null) {
            throw new IllegalArgumentException("SessionData cannot be null");
        }

        // 필수 필드 검증
        if (sessionData.getUserId() == null || sessionData.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("userId is required");
        }

        // 현재 시간 설정
        LocalDateTime now = LocalDateTime.now();

        // 동일한 사용자와 비디오 ID로 기존 세션이 있는지 확인
        List<SessionData> existingSessions = sessionRepository.findByUserId(sessionData.getUserId());
        for (SessionData existingSession : existingSessions) {
            // 동일한 비디오 ID인 경우 기존 세션 업데이트
            if (sessionData.getVideoId() != null &&
                    sessionData.getVideoId().equals(existingSession.getVideoId())) {
                log.info("동일한 비디오의 기존 세션 업데이트: {}", existingSession.getSessionId());

                // 기존 세션 정보 업데이트
                existingSession.setLastAccessTime(now);
                existingSession.setStatus(SessionStatus.ACTIVE.name());
                existingSession.setIpAddress(sessionData.getIpAddress());

                // 만료 시간 갱신 (2주 후)
                existingSession.setExpiresAt(now.plus(14, ChronoUnit.DAYS));

                // 세션 업데이트
                sessionRepository.update(existingSession);
                return existingSession;
            }
        }

        // sessionId가 없는 경우 생성
        if (sessionData.getSessionId() == null || sessionData.getSessionId().trim().isEmpty()) {
            sessionData.setSessionId(UUID.randomUUID().toString());
        }

        // 기본값 설정
        if (sessionData.getLastAccessTime() == null) {
            sessionData.setLastAccessTime(now);
        }
        if (sessionData.getStatus() == null) {
            sessionData.setStatus(SessionStatus.ACTIVE.name());
        }
        if (sessionData.getType() == null) {
            sessionData.setType(determineSessionType(sessionData.getDeviceInfo()));
        }

        // 생성 시간 설정
        sessionData.setCreatedAt(now);

        // 만료 시간 설정 (2주 후)
        sessionData.setExpiresAt(now.plus(14, ChronoUnit.DAYS));

        sessionRepository.save(sessionData);
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
        return sessionRepository.findBySessionId(sessionId).orElse(null);
    }

    @Override
    public SessionData refreshSession(String sessionId) {
        SessionData session = getSession(sessionId);
        if (session != null) {
            session.setLastAccessTime(LocalDateTime.now());
            sessionRepository.update(session);
            return session;
        }
        return null;
    }

    @Override
    public Optional<SessionData> deleteSession(String sessionId) {
        Optional<SessionData> session = sessionRepository.findBySessionId(sessionId);
        if (session.isPresent()) {
            sessionRepository.deleteBySessionId(sessionId);
            return session;
        }
        return Optional.empty();
    }

    @Override
    public List<SessionData> getUserSessions(String userId) {
        return sessionRepository.findByUserId(userId);
    }

    @Override
    public List<SessionData> getActiveSessions() {
        return sessionRepository.findAll().stream()
                .filter(session -> SessionStatus.ACTIVE.name().equals(session.getStatus()))
                .toList();
    }

    @Override
    public SessionData updateSessionStatus(String sessionId, SessionStatus status) {
        SessionData session = getSession(sessionId);
        if (session != null) {
            session.setStatus(status.name());
            sessionRepository.update(session);
            return session;
        }
        return null;
    }

    @Override
    public List<SessionData> getAllSessions() {
        return sessionRepository.findAll();
    }

    @Override
    public SessionData updateSession(SessionData sessionData) {
        sessionRepository.update(sessionData);
        return sessionData;
    }

    @Override
    public List<SessionData> deleteAllSessions() {
        List<SessionData> allSessions = getAllSessions();
        allSessions.forEach(session -> sessionRepository.deleteBySessionId(session.getSessionId()));
        return allSessions;
    }

    @Override
    public SessionData findSessionByUserAndVideo(String userId, String videoId) {
        log.info("세션 검색: 사용자 {}, 비디오 {}", userId, videoId);

        // 사용자의 모든 세션 조회
        List<SessionData> userSessions = sessionRepository.findByUserId(userId);

        // 동일한 비디오에 대한 세션 찾기
        return userSessions.stream()
                .filter(session -> videoId.equals(session.getVideoId()))
                .findFirst()
                .orElse(null);
    }
}