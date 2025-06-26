package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KeywordAlertResponseDto {
    private Long no;
    private String keyword;
    private LocalDateTime createdAt;
}
