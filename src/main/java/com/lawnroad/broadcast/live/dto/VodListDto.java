package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VodListDto {
    private Long vodNo;
    private String title;
    private String categoryName;
    private String thumbnailPath;
    private String lawyerName;
    private String lawyerProfile;
    private String vodPath;
    private Integer duration;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private List<String> keywords;
}
