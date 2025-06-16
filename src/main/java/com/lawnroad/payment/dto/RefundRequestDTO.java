package com.lawnroad.payment.dto;

import lombok.Data;

@Data
public class RefundRequestDTO {
    private String paymentKey;
    private Long amount;
    private String cancelReason;
}
