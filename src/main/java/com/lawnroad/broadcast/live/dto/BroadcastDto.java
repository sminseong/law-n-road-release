package com.lawnroad.broadcast.live.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastDto {
    private Long no;
    private Long userNo;
    private Long scheduleNo;
    private String sessionId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;        // ENUM: READY, RECORD, DONE, DELETED
    private Integer reportStatus; // 0 또는 1
    private LocalDateTime createdAt;
}
