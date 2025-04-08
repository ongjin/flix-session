package com.zerry.session.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.zerry.session.dto.SessionData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SessionRedisRepository {
    private static final String SESSION_KEY_PREFIX = "session:";
    private static final String USER_SESSIONS_KEY_PREFIX = "user_sessions:";
    private final RedisTemplate<String, Object> redisTemplate;

    public void save(SessionData sessionData) {
        String sessionKey = SESSION_KEY_PREFIX + sessionData.getSessionId();
        String userSessionsKey = USER_SESSIONS_KEY_PREFIX + sessionData.getUserId();

        redisTemplate.opsForValue().set(sessionKey, sessionData, 30, TimeUnit.MINUTES);
        redisTemplate.opsForList().rightPush(userSessionsKey, sessionData.getSessionId().toString());
    }

    public Optional<SessionData> findBySessionId(String sessionId) {
        String key = SESSION_KEY_PREFIX + sessionId;
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value instanceof SessionData) {
                return Optional.of((SessionData) value);
            } else if (value instanceof String) {
                // If we have a string value, it might be a session ID
                log.warn("Found string value instead of SessionData for key: {}", key);
                return Optional.empty();
            } else if (value != null) {
                log.warn("Unexpected value type for key {}: {}", key, value.getClass());
                return Optional.empty();
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error retrieving session for key {}: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    public List<SessionData> findByUserId(Long userId) {
        String userSessionsKey = USER_SESSIONS_KEY_PREFIX + userId;
        List<Object> sessionIds = redisTemplate.opsForList().range(userSessionsKey, 0, -1);

        if (sessionIds == null || sessionIds.isEmpty()) {
            return List.of();
        }

        return sessionIds.stream()
                .map(id -> {
                    if (id instanceof String) {
                        return findBySessionId((String) id);
                    } else {
                        log.warn("Unexpected session ID type: {}", id != null ? id.getClass() : "null");
                        return Optional.<SessionData>empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public void deleteBySessionId(String sessionId) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        Optional<SessionData> session = findBySessionId(sessionId);

        if (session.isPresent()) {
            String userSessionsKey = USER_SESSIONS_KEY_PREFIX + session.get().getUserId();
            redisTemplate.opsForList().remove(userSessionsKey, 0, sessionId);
        }

        redisTemplate.delete(sessionKey);
    }

    public List<SessionData> findAll() {
        return redisTemplate.keys(SESSION_KEY_PREFIX + "*").stream()
                .map(key -> {
                    try {
                        Object value = redisTemplate.opsForValue().get(key);
                        if (value instanceof SessionData) {
                            return (SessionData) value;
                        }
                        return null;
                    } catch (Exception e) {
                        log.error("Error retrieving session for key {}: {}", key, e.getMessage());
                        return null;
                    }
                })
                .filter(session -> session != null)
                .toList();
    }

    public void update(SessionData sessionData) {
        String sessionKey = SESSION_KEY_PREFIX + sessionData.getSessionId();

        // 세션 데이터 업데이트 및 TTL 갱신 (30분)
        redisTemplate.opsForValue().set(sessionKey, sessionData, 30, TimeUnit.MINUTES);
    }
}