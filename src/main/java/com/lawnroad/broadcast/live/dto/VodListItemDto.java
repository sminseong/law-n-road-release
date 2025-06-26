package com.lawnroad.broadcast.live.dto;

import lombok.Data;

@Data
public class VodListItemDto {
    private Long vodNo;
    private String title;
    private String thumbnailPath;
    private Long broadcastNo;
}
