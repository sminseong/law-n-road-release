package com.lawnroad.broadcast.live.dto;

import lombok.Data;

@Data
public class BroadcastSessionDto {
    private Long broadcastNo;
    private String sessionId;
    private String status;
}
