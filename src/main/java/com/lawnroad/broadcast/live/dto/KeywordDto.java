package com.lawnroad.broadcast.live.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordDto {
    private Long no;
    private Long scheduleNo;
    private String keyword;
    private LocalDateTime createdAt;
}
