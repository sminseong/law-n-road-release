package com.lawnroad.broadcast.live.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BroadcastVo {
    private Long no;              // 방송 no (PK)
    private Long userNo;          // 유저 no (FK)
    private Long scheduleNo;      // 스케줄 no (FK)
    private String sessionId;     // OpenVidu 세션 ID
    private LocalDateTime startTime;   // 방송 시작 시간
    private LocalDateTime endTime;     // 방송 종료 시간
    private String status;        // 방송 상태: READY, RECODE, DONE, DELETED
    private Integer reportStatus; // 신고 여부: 0 또는 1
    private LocalDateTime createdAt; // 생성일시
}
