package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BroadcastRequestDto {
    private Long userNo;       // 방송자
    private Long scheduleNo;   // 연관된 스케줄
    private String sessionId;  // OpenVidu 세션 ID
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
