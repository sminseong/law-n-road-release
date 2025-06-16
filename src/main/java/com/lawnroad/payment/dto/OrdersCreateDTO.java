package com.lawnroad.payment.dto;

import lombok.Data;

@Data
public class OrdersCreateDTO {
    private Long no;
    private String orderCode;
    private Long userNo;
    private Long amount;
    private String status;
    private String orderType;
}