package com.lawnroad.payment.dto;

import lombok.Data;

@Data
public class RefundRequestDTO {
    private Long reservationNo;
    private Long orderNo;
    private String paymentKey;
    private Long amount;
    private String cancelReason;
}
