package com.zerry.session.model;

public enum SessionStatus {
    ACTIVE, // 활성 세션
    INACTIVE, // 비활성 세션
    EXPIRED, // 만료된 세션
    TERMINATED, // 종료된 세션
    ERROR // 오류 상태
}