package com.lawnroad.broadcast.live.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleCalendarDto {
    private Long scheduleNo;
    private String title;
    private LocalDate date;
    private LocalDateTime startTime;

    private String lawyerName;
}
