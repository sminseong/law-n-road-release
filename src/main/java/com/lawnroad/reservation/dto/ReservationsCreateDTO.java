package com.lawnroad.reservation.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class ReservationsCreateDTO {
    private Long no;
    private Long orderNo;
    private Long slotNo;
    private Long userNo;
    private String content;

    private LocalTime slotTime;
    private Long amount;
}
