package com.lawnroad.broadcast.live.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewerCountDto {
    private Long broadcastNo;
    private Integer viewerCount;
}
