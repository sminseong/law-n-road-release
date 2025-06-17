package com.lawnroad.broadcast.live.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BroadcastReportVo {
    private Long no;
    private Long broadcastNo;
    private Long userNo;
    private String reasonCode;
    private String detailReason;
    private LocalDateTime createdAt;
}
