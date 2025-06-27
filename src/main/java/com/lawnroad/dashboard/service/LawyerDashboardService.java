package com.lawnroad.dashboard.service;

import com.lawnroad.dashboard.dto.TodayScheduleDto;
import com.lawnroad.dashboard.dto.TomorrowBroadcastDto;
import com.lawnroad.dashboard.dto.TomorrowConsultationRequestDto;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface LawyerDashboardService {
    /**
     * 오늘 일정만 조회
     * @param lawyerNo 변호사 번호
     * @return 오늘 일정 리스트
     */
    List<TodayScheduleDto> getTodaySchedule(Long lawyerNo);
    /**
     * 내일 상담 신청 목록 조회
     * @return 내일 상담 신청 목록
     */
    List<TomorrowConsultationRequestDto> getTomorrowConsultationRequests();
    // 파라미터 없이 내일 방송을 조회
    List<TomorrowBroadcastDto> getTomorrowBroadcasts();
}
