package com.zerry.session.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SessionData
 * Redis에 저장될 세션 정보 모델
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "SessionData", timeToLive = 3600) // TTL 3600초
public class SessionData {
    @Id
    private String sessionId; // userId + "-" + deviceId
    private String userId; // 사용자 ID
    private Long contentId; // 재생 중인 콘텐츠 ID
    private String deviceId; // 디바이스 식별자(예: "WEB", "MOBILE_12")
    private int lastPosition; // 마지막 재생 위치 (초 단위)
    private String deviceInfo; // 디바이스 정보 (예: Web, iOS 등)
    private String status; // 재생 상태 (예: playing, paused 등)
    private Instant createdAt;
    private Instant updatedAt;
}
