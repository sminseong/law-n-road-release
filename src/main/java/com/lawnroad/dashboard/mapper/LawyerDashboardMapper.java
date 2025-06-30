package com.lawnroad.dashboard.mapper;

import com.lawnroad.dashboard.dto.MonthlyRevenueDto;
import com.lawnroad.dashboard.dto.TomorrowBroadcastDto;
import com.lawnroad.dashboard.dto.TomorrowConsultationRequestDto;
import com.lawnroad.dashboard.dto.TodayScheduleDto;
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
    List<TomorrowConsultationRequestDto> selectTomorrowConsultationRequests(@Param("lawyerNo") Long lawyerNo);

    List<TomorrowBroadcastDto> selectTomorrowBroadcasts(@Param("date") LocalDate date);
    
    /**
     * 거니짱
     * 월별 상담 예약 + 템플릿 판매 수익 조회
     * @param lawyerNo 변호사 번호
     * @return List of MonthlyRevenueDto
     */
    List<MonthlyRevenueDto> selectMonthlySalesRevenue(@Param("lawyerNo") Long lawyerNo);
}
