package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BroadcastResponseDto {
    private Long broadcastNo;
    private String title;           // schedule.name 조인
    private String categoryName;    // category 조인
    private String lawyerName;      // user.name 조인
    private String thumbnailPath;   // schedule.thumbnail_path
    private String status;          // READY, RECODE, DONE, DELETED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
