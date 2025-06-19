package com.lawnroad.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RefundSaveDTO {
    private Long paymentNo;
    private String refundKey;
    private Long amount;
    private String status;
    private String reason;
    private LocalDateTime refundedAt;
    private String metadata;
}
