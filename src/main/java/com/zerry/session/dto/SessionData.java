package com.zerry.session.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private String sessionType;
    private String status;
    private String lastAccessTimeStr; // LocalDateTime을 ISO 문자열로 저장
    private String createdAtStr; // LocalDateTime을 ISO 문자열로 저장
    private String expiresAtStr; // LocalDateTime을 ISO 문자열로 저장
    private SessionType type;
    private Integer lastPosition;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // LocalDateTime을 문자열로 변환
    public void setLastAccessTime(LocalDateTime lastAccessTime) {
        this.lastAccessTimeStr = lastAccessTime != null ? lastAccessTime.format(formatter) : null;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAtStr = createdAt != null ? createdAt.format(formatter) : null;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAtStr = expiresAt != null ? expiresAt.format(formatter) : null;
    }

    // 문자열을 LocalDateTime으로 변환
    public LocalDateTime getLastAccessTime() {
        return lastAccessTimeStr != null ? LocalDateTime.parse(lastAccessTimeStr, formatter) : null;
    }

    public LocalDateTime getCreatedAt() {
        return createdAtStr != null ? LocalDateTime.parse(createdAtStr, formatter) : null;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAtStr != null ? LocalDateTime.parse(expiresAtStr, formatter) : null;
    }
}