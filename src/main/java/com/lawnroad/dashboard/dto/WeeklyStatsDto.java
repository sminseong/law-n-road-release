package com.lawnroad.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 주간 상담 & 방송 통계 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyStatsDto {
    private int dayOfWeek;
    private int consultationCount;
    private int broadcastCount;
    private String dayName;

    public String getDayName() {
        switch(dayOfWeek) {
            case 1: return "일요일";
            case 2: return "월요일";
            case 3: return "화요일";
            case 4: return "수요일";
            case 5: return "목요일";
            case 6: return "금요일";
            case 7: return "토요일";
            default: return "알 수 없음";
        }
    }
}
