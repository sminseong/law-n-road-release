package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BroadcastVodResponseDto {
    private Long vodNo;
    private String title;         // schedule.name (조인)
    private String categoryName;  // category.name (조인)
    private String lawyerName;    // user.name (조인)
    private String thumbnailPath; // schedule.thumbnail_path
    private String vodPath;
    private Integer duration;
    private LocalDateTime createdAt;
}
