package com.lawnroad.payment.dto;

import lombok.Data;

@Data
public class OrdersStatusUpdateDTO {
    private Long orderNo;
    private String status;
}