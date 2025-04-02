package com.zerry.session.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zerry.session.dto.PatchRequest;
import com.zerry.session.model.SessionData;
import com.zerry.session.response.ApiResponse;
import com.zerry.session.service.SessionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<ApiResponse<SessionData>> createSession(@RequestBody SessionData sessionData) {
        SessionData created = sessionService.createSession(sessionData);
        return ResponseEntity.ok(ApiResponse.success("세션 생성 완료", created));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<SessionData>> getSession(@PathVariable String sessionId) {
        SessionData found = sessionService.getSession(sessionId);
        if (found == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail("세션을 찾을 수 없습니다."));
        }
        return ResponseEntity.ok(ApiResponse.success(found));
    }

    /**
     * 전체 세션 조회 엔드포인트
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SessionData>>> getAllSessions() {
        List<SessionData> sessions = sessionService.getAllSessions();
        return ResponseEntity.ok(ApiResponse.success("전체 세션 조회 성공", sessions));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<SessionData>> updateSession(@RequestBody SessionData sessionData) {
        SessionData existing = sessionService.getSession(sessionData.getSessionId());
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail("세션을 찾을 수 없습니다."));
        }
        SessionData updated = sessionService.updateSession(sessionData);
        return ResponseEntity.ok(ApiResponse.success("세션 업데이트 완료", updated));
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
            existing.setStatus(patchRequest.getStatus());
        }
        if (patchRequest.getDeviceInfo() != null) {
            existing.setDeviceInfo(patchRequest.getDeviceInfo());
        }

        // 실제 저장
        sessionService.updateSession(existing);

        return ResponseEntity.ok(ApiResponse.success("세션 부분 업데이트 완료", existing));
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<?>> deleteSession(@PathVariable String sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success("세션 삭제 완료", null));
    }

    /**
     * 전체 세션 삭제 엔드포인트
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<?>> deleteAllSessions() {
        sessionService.deleteAllSessions();
        return ResponseEntity.ok(ApiResponse.success("전체 세션 삭제 완료", null));
    }
}
