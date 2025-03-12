package com.zerry.flix_session.dto;

import lombok.Data;

@Data
public class PatchRequest {
    private Integer lastPosition; // null이면 업데이트 안 함
    private String status; // null이면 업데이트 안 함
    private String deviceInfo; // etc...
}
