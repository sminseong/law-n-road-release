package com.lawnroad.reservation.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TimeSlotResponseDTO {
    private Long no;
    private LocalDate slotDate;
    private LocalTime slotTime;
    private int status;
    private Long amount;
}
