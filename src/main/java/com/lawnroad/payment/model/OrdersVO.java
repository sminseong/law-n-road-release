package com.lawnroad.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdersVO {
    private Long no;
    private String orderCode;
    private Long userNo;
    private Long amount;
    private String status;
    private String orderType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}