package com.lawnroad.payment.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentVO {
    private Long no;
    private Long orderNo;
    private String paymentKey;
    private String orderCode;
    private Long amount;
    private String status;
    private Integer installmentMonth;
    private LocalDateTime purchasedAt;
    private String pg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
