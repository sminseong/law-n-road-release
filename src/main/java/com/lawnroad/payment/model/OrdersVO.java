package com.lawnroad.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrdersVO {
    private Long no;
    private Long userNo;
    private Long totalAmount;
    private String status;
    private String orderType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}