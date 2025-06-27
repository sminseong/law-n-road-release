package com.lawnroad.account.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BroadcastReportConfirmDTO {
    private Long no;
    private Long userNo;
    private Long broadcastNo;
    private String reason;
    private String detailReason;
    private LocalDateTime createdAt;
    private int status;
}
