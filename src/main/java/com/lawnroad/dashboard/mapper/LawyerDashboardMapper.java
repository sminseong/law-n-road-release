package com.lawnroad.dashboard.mapper;

import com.lawnroad.dashboard.dto.TomorrowBroadcastDto;
import com.lawnroad.dashboard.dto.TomorrowConsultationRequestDto;
import com.lawnroad.dashboard.dto.TodayScheduleDto;
import com.lawnroad.dashboard.dto.WeeklyStatsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LawyerDashboardMapper {
    /**
     * 변호사의 오늘 일정 조회 (상담 + 방송)
     * @param lawyerNo 변호사 번호
     * @return 오늘 일정 리스트
     */
    List<TodayScheduleDto> getTodaySchedule(@Param("lawyerNo") Long lawyerNo);
    /**
     * 내일 상담 신청 목록 조회
     * @return 내일 상담 신청 목록
     */
    List<TomorrowConsultationRequestDto> selectTomorrowConsultationRequests();

    List<TomorrowBroadcastDto> selectTomorrowBroadcasts(@Param("date") LocalDate date, @Param("userNo") Long userNo);

    /**
     * 특정 변호사의 주간 상담 & 방송 통계 조회
     * @param lawyerNo 변호사 번호
     * @return 주간 통계 리스트 (월~일요일)
     */
    List<WeeklyStatsDto> getWeeklyStats(@Param("lawyerNo") Long lawyerNo);
}
