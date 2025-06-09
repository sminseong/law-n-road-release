package com.lawnroad.broadcast.live.dto;

import lombok.Data;

@Data
public class BroadcastVodRequestDto {
    private Long broadcastNo;
    private String vodPath;   // ex: /vods/2025/06/08/broadcast1234.mp4
    private Integer duration; // 초 단위
}
