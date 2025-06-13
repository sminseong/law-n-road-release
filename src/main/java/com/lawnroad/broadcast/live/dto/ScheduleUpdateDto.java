package com.lawnroad.broadcast.live.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleUpdateDto {
    private Long scheduleNo;
    private Long categoryNo;
    private String name;
    private String content;
    private String thumbnailPath;
    private List<String> keywords;
}
