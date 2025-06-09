package com.lawnroad.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor
public class TimeSlotUpdateRequest {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate slotDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime slotTime;

    private int status;
}
