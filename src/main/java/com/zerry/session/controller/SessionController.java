package com.zerry.session.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zerry.session.dto.PatchRequest;
import com.zerry.session.dto.SessionData;
import com.zerry.session.dto.SessionSearchRequest;
import com.zerry.session.dto.StatusUpdateRequest;
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
    public ResponseEntity<ApiResponse<SessionData>> getSession(@PathVariable String sessionId) {
        SessionData session = sessionService.getSession(sessionId);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("세션을 찾을 수 없습니다."));
        }
        return ResponseEntity.ok(ApiResponse.success("세션 조회 완료", session));
    }

    @PutMapping("/{sessionId}/refresh")
    public ResponseEntity<ApiResponse<SessionData>> refreshSession(@PathVariable String sessionId) {
        SessionData session = sessionService.refreshSession(sessionId);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("세션을 찾을 수 없습니다."));
        }
        return ResponseEntity.ok(ApiResponse.success("세션 갱신 완료", session));
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<SessionData>> deleteSession(@PathVariable String sessionId) {
        Optional<SessionData> deletedSession = sessionService.deleteSession(sessionId);
        if (deletedSession.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("세션 삭제 완료", deletedSession.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("세션을 찾을 수 없습니다: " + sessionId));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<SessionData>>> getUserSessions(@PathVariable Long userId) {
        List<SessionData> sessions = sessionService.getUserSessions(userId);
        return ResponseEntity.ok(ApiResponse.success("유저 세션 조회 완료", sessions));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SessionData>>> getActiveSessions() {
        List<SessionData> sessions = sessionService.getActiveSessions();
        return ResponseEntity.ok(ApiResponse.success("활성 세션 목록 조회 완료", sessions));
    }

    @PutMapping("/{sessionId}/status")
    public ResponseEntity<ApiResponse<SessionData>> updateSessionStatus(
            @PathVariable String sessionId,
            @RequestBody StatusUpdateRequest request) {
        SessionData session = sessionService.updateSessionStatus(sessionId, request.getStatus());
        if (session == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("세션을 찾을 수 없습니다."));
        }
        return ResponseEntity.ok(ApiResponse.success("세션 상태 업데이트 완료", session));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SessionData>>> getAllSessions() {
        List<SessionData> sessions = sessionService.getAllSessions();
        return ResponseEntity.ok(ApiResponse.success("전체 세션 목록 조회 완료", sessions));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<SessionData>> updateSession(@RequestBody SessionData sessionData) {
        // 세션 존재 여부 확인
        SessionData existingSession = sessionService.getSession(sessionData.getSessionId());
        if (existingSession == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("세션을 찾을 수 없습니다."));
        }

        // 세션 업데이트
        SessionData updated = sessionService.updateSession(sessionData);
        return ResponseEntity.ok(ApiResponse.success("세션 정보 업데이트 완료", updated));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<List<SessionData>>> deleteAllSessions() {
        List<SessionData> deletedSessions = sessionService.deleteAllSessions();
        return ResponseEntity.ok(ApiResponse.success("전체 세션 삭제 완료", deletedSessions));
    }

    @PatchMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<SessionData>> patchSession(
            @PathVariable String sessionId,
            @RequestBody PatchRequest patchRequest) {
        SessionData existing = sessionService.getSession(sessionId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("세션을 찾을 수 없습니다."));
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
        SessionData updated = sessionService.updateSession(existing);
        return ResponseEntity.ok(ApiResponse.success("세션 부분 업데이트 완료", updated));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<SessionData>> searchSession(@RequestBody SessionSearchRequest request) {
        log.info("세션 검색 요청: 사용자 {}, 비디오 {}", request.getUsername(), request.getVideoId());

        // 세션 검색
        SessionData existingSession = sessionService.findSessionByUserAndVideo(
                request.getUserId(), request.getVideoId());

        if (existingSession != null) {
            log.info("기존 세션 발견: {}", existingSession.getSessionId());
            return ResponseEntity.ok(ApiResponse.success("기존 세션을 찾았습니다.", existingSession));
        }

        log.info("기존 세션 없음");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail("세션을 찾을 수 없습니다."));
    }
}
