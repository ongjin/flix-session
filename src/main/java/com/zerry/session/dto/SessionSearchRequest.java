package com.zerry.session.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionSearchRequest {
    private String username;
    private Long userId;
    private String videoId;
}