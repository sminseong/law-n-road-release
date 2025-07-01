package com.lawnroad.dashboard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyCountDto {
    /** YYYY-MM-DD 형태의 날짜 */
    private LocalDate date;
    /** 해당 날짜의 건수 */
    private int count;
}
