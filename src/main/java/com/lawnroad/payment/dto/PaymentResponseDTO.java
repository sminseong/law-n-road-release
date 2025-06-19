package com.lawnroad.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponseDTO {
    private String paymentKey;
    private String orderId;
    private Long amount;
    private String status;        // "DONE", "FAILED", ...
    private LocalDateTime approvedAt;
    private String metadata;
    private String method;        // 카드, 가상계좌, ...
    private Card card;
    private String pg;
    private List<CancelDTO> cancels;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Card {
        private String company;              // 카드사
        private Integer installmentPlanMonths;  // 할부 개월 수
    }
}