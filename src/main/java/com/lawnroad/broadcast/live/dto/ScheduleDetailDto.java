package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ScheduleDetailDto {
    private Long scheduleNo;
    private Long userNo;
    private Long categoryNo;
    private String categoryName;
    private String name;
    private String content;
    private String thumbnailPath;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // 키워드 리스트
    private List<String> keywords;
}
