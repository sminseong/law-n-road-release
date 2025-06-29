package com.lawnroad.dashboard.service;

import com.lawnroad.dashboard.dto.*;

import java.util.List;

public interface LawyerDashboardService {
    /**
     * 오늘 일정만 조회
     *  @param userNo 변호사(유저) 번호
     * @return 오늘 일정 리스트
     */
    List<TodayScheduleDto> getTodaySchedule(Long userNo);
    /**
     * 내일 상담 신청 목록 조회
     * @param userNo 변호사(유저) 번호
     * @return 내일 상담 신청 목록
     */
    List<TomorrowConsultationRequestDto> getTomorrowConsultationRequests(Long userNo);
    /**
     * 내일 방송 목록 조회
     * @param userNo 변호사(유저) 번호
     * @return 내일 방송 리스트
     */
    List<TomorrowBroadcastDto> getTomorrowBroadcasts(Long userNo);

    List<DailyCountDto> getWeeklyConsultations(Long userNo);
    List<DailyCountDto> getWeeklyBroadcasts(Long userNo);

    MonthlyRevenueDto getMonthlyRevenue(Long userNo);
    /**
     * 이달의 템플릿 판매 건수 조회
     * @param userNo 변호사 번호
     * @return 이달의 템플릿 판매 건수
     */
    MonthlyTemplateSalesDto getMonthlyTemplateSales(Long userNo);
}
