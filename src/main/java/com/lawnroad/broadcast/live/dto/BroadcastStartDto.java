package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BroadcastStartDto {
    private Long scheduleNo;
    private String sessionId;  // OpenVidu에서 발급받은 세션 ID
}
