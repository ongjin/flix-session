package com.zerry.flix_session.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionEvent {
    private String action;
    private String sessionId;
    private String updatedInfo;
}
