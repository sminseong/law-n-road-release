package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleResponseDto {
    private Long no;
    private String name;
    private String thumbnailPath;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String categoryName;
    private String lawyerName;
}
