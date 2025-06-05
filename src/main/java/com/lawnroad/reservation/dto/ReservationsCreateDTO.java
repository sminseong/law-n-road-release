package com.lawnroad.reservation.dto;

import lombok.Data;

@Data
public class ReservationsCreateDTO {
    private Long orderNo;
    private Long slotNo;
    private Long userNo;
    private String content;
}
