package com.lawnroad.broadcast.live.dto;

import lombok.Data;

@Data
public class ScheduleResponseDto {
    private Long scheduleNo;
    private String title;
    private String date;
    private String startTime;
    private String endTime;
    private String categoryName;
}
