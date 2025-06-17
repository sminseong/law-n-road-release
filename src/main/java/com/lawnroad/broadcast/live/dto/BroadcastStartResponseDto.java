package com.lawnroad.broadcast.live.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastStartResponseDto {
    private String sessionId;
    private String token;
    private Long broadcastNo;
    private LocalDateTime startTime;
}
