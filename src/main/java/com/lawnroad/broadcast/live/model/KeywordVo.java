package com.lawnroad.broadcast.live.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KeywordVo {
    private Long no;              // 키워드 no (PK)
    private Long broadcastNo;     // 방송 no (FK)
    private String keyword;       // 키워드 문자열
    private LocalDateTime createdAt; // 생성일시
}
