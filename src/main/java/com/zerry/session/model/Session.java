package com.zerry.session.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String userId;
    private String deviceInfo;
    private String ipAddress;
    private LocalDateTime lastAccessTime;
    private LocalDateTime expiryTime;
    private SessionStatus status;
    private SessionType type;
}