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
    private String userId;
    private String deviceInfo;
    private String ipAddress;
    private String sessionType;
    private String status;
    private LocalDateTime lastAccessTimeStr;
    private LocalDateTime createdAtStr;
    private LocalDateTime expiresAtStr;
    private SessionType type;
    private Integer lastPosition;

}