package com.zerry.session.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.zerry.session.model.SessionData;
import com.zerry.session.model.SessionStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryImpl implements SessionRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final long SESSION_TIMEOUT = 1L; // 1시간

    @Override
    public void saveSession(SessionData sessionData) {
        String key = "session:" + sessionData.getSessionId();
        redisTemplate.opsForValue().set(key, sessionData, SESSION_TIMEOUT, TimeUnit.HOURS);
    }

    @Override
    public Optional<SessionData> findBySessionId(String sessionId) {
        String key = "session:" + sessionId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof SessionData) {
            return Optional.of((SessionData) value);
        }
        return Optional.empty();
    }

    @Override
    public List<SessionData> findByUserId(String userId) {
        // Redis에서는 직접적인 쿼리가 어려우므로, 모든 세션을 가져와서 필터링
        return redisTemplate.keys("session:*")
                .stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .filter(value -> value instanceof SessionData)
                .map(value -> (SessionData) value)
                .filter(session -> session.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<SessionData> findByStatus(SessionStatus status) {
        return redisTemplate.keys("session:*")
                .stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .filter(value -> value instanceof SessionData)
                .map(value -> (SessionData) value)
                .filter(session -> SessionStatus.valueOf(session.getStatus()) == status)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSession(String sessionId) {
        String key = "session:" + sessionId;
        redisTemplate.delete(key);
    }

    @Override
    public void updateSessionTTL(String sessionId, long ttlSeconds) {
        String key = "session:" + sessionId;
        if (redisTemplate.hasKey(key)) {
            redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }
    }
}
