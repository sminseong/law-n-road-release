package com.lawnroad.broadcast.live.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequestDto {
    private Long userNo;
    private Long categoryNo;
    private String name;
    private String content;
    private String thumbnailPath; // 프론트에서 파일 업로드 후 경로만 넘겨줄 수도 있음
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<String> keywords;
}
