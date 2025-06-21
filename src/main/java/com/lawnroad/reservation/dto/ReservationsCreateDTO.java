package com.lawnroad.reservation.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class ReservationsCreateDTO {
    private Long no;
    private Long orderNo;
    private String orderCode;
    private Long slotNo;
    private Long userNo;

    private LocalTime slotTime;
    private Long amount;
}
