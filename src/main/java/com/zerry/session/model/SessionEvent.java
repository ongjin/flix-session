package com.zerry.session.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionEvent {
    private String type; // 이벤트 타입 (connected, heartbeat, status_update, error 등)
    private String userId; // 사용자 ID
    private String message; // 이벤트 메시지
    private long timestamp; // 이벤트 발생 시간

    public SessionEvent(String type, String userId, String message) {
        this.type = type;
        this.userId = userId;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}