package com.lawnroad.reservation.dto;

import lombok.Data;

@Data
public class ReservationsUpdateDTO {
    private Long reservationNo;
    private String status;
}
