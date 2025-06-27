package com.lawnroad.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayScheduleDto {
    private String time;        // 시간
    private String event;       // 이벤트명
    private String type;        // consultation, broadcast
    private String clientName;  // 클라이언트명 (상담의 경우만)
}
