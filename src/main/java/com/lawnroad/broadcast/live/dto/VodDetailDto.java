package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VodDetailDto {
    private Long vodNo;
    private Long broadcastNo;
    private String vodPath;
    private Integer duration;
    private Integer viewCount;
    private LocalDateTime createdAt;

    private Long scheduleNo;
    private String title;
    private String content;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String thumbnailPath;

    private String categoryName;
    private Long lawyerNo;
    private String lawyerName;
    private String lawyerProfile;

    private List<String> keywords;
}
