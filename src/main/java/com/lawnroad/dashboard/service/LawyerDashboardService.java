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
    /**
     * 특정 변호사의 주간 상담 & 방송 통계 조회
     * @param userNo 변호사(유저) 번호
     * @return 주간 통계 맵 (consultations, broadcasts 배열 포함)
     */
    MonthlyRevenueDto getMonthlyRevenue(Long userNo);
}
