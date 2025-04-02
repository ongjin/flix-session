package com.zerry.session.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zerry.session.dto.PatchRequest;
import com.zerry.session.dto.SessionData;
import com.zerry.session.model.SessionStatus;
import com.zerry.session.response.ApiResponse;
import com.zerry.session.service.SessionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<ApiResponse<SessionData>> createSession(@RequestBody SessionData sessionData) {
        SessionData created = sessionService.createSession(sessionData);
        return ResponseEntity.ok(ApiResponse.success("세션 생성 완료", created));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionData> getSession(@PathVariable String sessionId) {
        return ResponseEntity.ok(sessionService.getSession(sessionId));
    }

    @PutMapping("/{sessionId}/refresh")
    public ResponseEntity<Void> refreshSession(@PathVariable String sessionId) {
        sessionService.refreshSession(sessionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable String sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SessionData>> getUserSessions(@PathVariable String userId) {
        return ResponseEntity.ok(sessionService.getUserSessions(userId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<SessionData>> getActiveSessions() {
        return ResponseEntity.ok(sessionService.getActiveSessions());
    }

    @PutMapping("/{sessionId}/status")
    public ResponseEntity<Void> updateSessionStatus(
            @PathVariable String sessionId,
            @RequestBody SessionStatus status) {
        sessionService.updateSessionStatus(sessionId, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<SessionData>> getAllSessions() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }

    @PutMapping
    public ResponseEntity<SessionData> updateSession(@RequestBody SessionData sessionData) {
        return ResponseEntity.ok(sessionService.updateSession(sessionData));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllSessions() {
        sessionService.deleteAllSessions();
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<SessionData>> patchSession(
            @PathVariable String sessionId,
            @RequestBody PatchRequest patchRequest) {

        SessionData existing = sessionService.getSession(sessionId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail("세션을 찾을 수 없습니다."));
        }
        // 부분 필드만 갱신
        if (patchRequest.getLastPosition() != null) {
            existing.setLastPosition(patchRequest.getLastPosition());
        }
        if (patchRequest.getStatus() != null) {
            existing.setStatus(SessionStatus.valueOf(patchRequest.getStatus()));
        }
        if (patchRequest.getDeviceInfo() != null) {
            existing.setDeviceInfo(patchRequest.getDeviceInfo());
        }

        // 실제 저장
        sessionService.updateSession(existing);

        return ResponseEntity.ok(ApiResponse.success("세션 부분 업데이트 완료", existing));
    }
}
