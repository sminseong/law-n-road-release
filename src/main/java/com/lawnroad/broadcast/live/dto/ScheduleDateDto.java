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
public class ScheduleDateDto {
    private Long no;
    private String name;
    private String content;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String lawyerName;
}
