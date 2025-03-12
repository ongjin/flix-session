package com.zerry.flix_session.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.zerry.flix_session.websocket.SessionWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSoketConfig implements WebSocketConfigurer {
    private final SessionWebSocketHandler sessionWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // "/ws/session" endpoint로 WebSocket 연결
        registry.addHandler(sessionWebSocketHandler, "/ws/session").setAllowedOrigins("*");
    }
}
