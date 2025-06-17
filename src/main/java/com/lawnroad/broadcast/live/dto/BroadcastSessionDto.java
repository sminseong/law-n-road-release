package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BroadcastSessionDto {
    private Long broadcastNo;
    private String sessionId;
    private String status;
    private LocalDateTime startTime;
}
