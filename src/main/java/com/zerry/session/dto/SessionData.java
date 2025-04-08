package com.zerry.session.dto;

import java.time.LocalDateTime;

import com.zerry.session.model.SessionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionData {
    private String sessionId;
    private Long userId;
    private String deviceInfo;
    private String ipAddress;
    private String status;
    private LocalDateTime lastAccessTime;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private SessionType type;
    private Integer lastPosition;
    private String videoId;
}