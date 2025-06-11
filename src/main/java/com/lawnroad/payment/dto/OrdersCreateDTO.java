package com.lawnroad.payment.dto;

import lombok.Data;

@Data
public class OrdersCreateDTO {
    private Long no;
    private Long userNo;
    private Long totalAmount;
    private String status;
    private String orderType;
}