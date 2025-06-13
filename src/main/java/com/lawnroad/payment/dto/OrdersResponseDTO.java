package com.lawnroad.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrdersResponseDTO {
    private Long no;
    private Long userNo;
    private Long totalAmount;
    private String status;
    private String orderType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}