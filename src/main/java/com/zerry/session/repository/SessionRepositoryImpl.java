package com.zerry.session.repository;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.zerry.session.model.SessionData;

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
    public SessionData findSessionById(String sessionId) {
        String key = "session:" + sessionId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof SessionData) {
            return (SessionData) value;
        }
        return null;
    }

    @Override
    public void deleteSession(String sessionId) {
        String key = "session:" + sessionId;
        redisTemplate.delete(key);
    }

    @Override
    public void updateSessionTTL(String sessionId, long ttlSeconds) {
        String key = "session:" + sessionId;
        // 키가 존재하면 남은 TTL을 새로 갱신
        if (redisTemplate.hasKey(key)) {
            redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }
    }
}
