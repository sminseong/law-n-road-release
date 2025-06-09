package com.lawnroad.broadcast.live.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ScheduleVo {
    private Long no;                // 스케줄 no (PK)
    private Long userNo;           // 유저 no (FK)
    private Long categoryNo;       // 카테고리 no (FK)
    private String name;           // 방송 제목
    private String content;        // 방송 설명
    private String thumbnailPath;  // 썸네일 경로 (nullable)
    private LocalDate date;        // 방송 예정 날짜
    private LocalDateTime startTime; // 방송 시작 시간
    private LocalDateTime endTime;   // 방송 종료 시간
    private LocalDateTime createdAt; // 생성일시
}
