package com.lawnroad.broadcast.live.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BroadcastVodVo {
    private Long no;              // VOD no (PK)
    private Long broadcastNo;     // 방송 no (FK)
    private String vodPath;       // VOD 파일 경로
    private Integer duration;     // 영상 길이 (초 단위)
    private Integer status;       // 0: 유지, 1: 삭제됨
    private LocalDateTime createdAt; // 생성일시
}
