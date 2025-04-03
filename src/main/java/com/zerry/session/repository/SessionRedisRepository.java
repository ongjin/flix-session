package com.zerry.session.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.zerry.session.dto.SessionData;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SessionRedisRepository {
    private static final String SESSION_KEY_PREFIX = "session:";
    private static final String USER_SESSIONS_KEY_PREFIX = "user_sessions:";
    private final RedisTemplate<String, Object> redisTemplate;

    public void save(SessionData sessionData) {
        String sessionKey = SESSION_KEY_PREFIX + sessionData.getSessionId();
        String userSessionsKey = USER_SESSIONS_KEY_PREFIX + sessionData.getUserId();

        redisTemplate.opsForValue().set(sessionKey, sessionData);
        redisTemplate.opsForList().rightPush(userSessionsKey, sessionData.getSessionId());
    }

    public Optional<SessionData> findBySessionId(String sessionId) {
        String key = SESSION_KEY_PREFIX + sessionId;
        return Optional.ofNullable((SessionData) redisTemplate.opsForValue().get(key));
    }

    public List<SessionData> findByUserId(String userId) {
        String userSessionsKey = USER_SESSIONS_KEY_PREFIX + userId;
        List<Object> sessionIds = redisTemplate.opsForList().range(userSessionsKey, 0, -1);

        if (sessionIds == null || sessionIds.isEmpty()) {
            return List.of();
        }

        return sessionIds.stream()
                .map(id -> findBySessionId((String) id))
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

    public void deleteByUserId(String userId) {
        String userSessionsKey = USER_SESSIONS_KEY_PREFIX + userId;
        List<Object> sessionIds = redisTemplate.opsForList().range(userSessionsKey, 0, -1);

        if (sessionIds != null) {
            sessionIds.forEach(id -> deleteBySessionId((String) id));
        }
    }

    public List<SessionData> findAll() {
        return redisTemplate.keys(SESSION_KEY_PREFIX + "*").stream()
                .map(key -> (SessionData) redisTemplate.opsForValue().get(key))
                .filter(session -> session != null)
                .toList();
    }

    public void update(SessionData sessionData) {
        save(sessionData);
    }
}