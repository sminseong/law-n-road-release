package com.lawnroad.broadcast.live.dto;

import lombok.Data;

@Data
public class VodPreviewDto {
    private Long vodNo;
    private Long broadcastNo;
    private String categoryName;
    private String title;
    private String thumbnailPath;
    private String vodPath;
    private Integer duration;
}
