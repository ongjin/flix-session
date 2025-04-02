package com.zerry.session.dto;

import java.time.LocalDateTime;

import com.zerry.session.model.SessionStatus;
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
    private String userId;
    private String deviceInfo;
    private String ipAddress;
    private LocalDateTime lastAccessTime;
    private LocalDateTime expiryTime;
    private SessionStatus status;
    private SessionType type;
    private Integer lastPosition;
}