package com.lawnroad.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponseDTO {
    private String paymentKey;
    private String orderId;
    private Integer totalAmount;
    private String status;        // "DONE", "FAILED", ...
    private String approvedAt;
    private String method;        // 카드, 가상계좌, ...
    private Card card;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Card {
        private String company;              // 카드사
        private Integer installmentPlanMonths;  // 할부 개월 수
    }
}