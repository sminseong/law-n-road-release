package com.lawnroad.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationCountDTO {
    private int requestedCount;
    private int doneCount;
}