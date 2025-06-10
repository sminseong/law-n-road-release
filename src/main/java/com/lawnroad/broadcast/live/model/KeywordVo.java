package com.lawnroad.broadcast.live.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class KeywordVo {
    private Long no;              // 키워드 no (PK)
    private Long scheduleNo;     // 방송 no (FK)
    private String keyword;       // 키워드 문자열
    private LocalDateTime createdAt; // 생성일시
}
