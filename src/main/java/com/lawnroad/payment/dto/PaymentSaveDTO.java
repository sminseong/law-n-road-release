package com.lawnroad.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class PaymentSaveDTO {
    private Long orderNo;
    private String paymentKey;
    private String orderCode;
    private Long amount;
    private String status;
    private String cardCompany;
    private Integer installmentMonth;
    private LocalDateTime purchasedAt;
    private String pg;
    private Map<String, Object> metadata;
}
