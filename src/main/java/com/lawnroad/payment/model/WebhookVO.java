package com.lawnroad.payment.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WebhookVO {
    private Long no;
    private Long paymentNo;
    private String eventType;
    private String payload;
    private LocalDateTime receivedAt;
    private Boolean processed;
    private String errorMessage;
}
