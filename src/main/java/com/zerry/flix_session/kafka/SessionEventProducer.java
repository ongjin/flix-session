package com.zerry.flix_session.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerry.flix_session.model.SessionData;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SessionEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendSessionEvent(String action, SessionData sessionData) {
        try {
            String payload = objectMapper.writeValueAsString(sessionData);
            // 예: "session-topic"에 { "action": "create/update/delete", "data": {세션정보} } 형태로
            // 발행
            String message = objectMapper.writeValueAsString(
                    new SessionEvent(action, payload));
            kafkaTemplate.send("session-topic", message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // 내부 클래스 혹은 별도 파일로 분리
    @RequiredArgsConstructor
    static class SessionEvent {
        public final String action;
        public final String data;
    }
}
