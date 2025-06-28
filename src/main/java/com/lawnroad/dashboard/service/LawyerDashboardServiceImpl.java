package com.lawnroad.dashboard.service;

import com.lawnroad.dashboard.dto.*;
import com.lawnroad.dashboard.mapper.LawyerDashboardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LawyerDashboardServiceImpl implements LawyerDashboardService {

    private final LawyerDashboardMapper lawyerDashboardMapper;

    @Override
    public List<TodayScheduleDto> getTodaySchedule(Long userNo) {

        try {
            List<TodayScheduleDto> schedules = lawyerDashboardMapper.getTodaySchedule(userNo);
            return schedules;
        } catch (Exception e) {
            throw new RuntimeException("오늘 일정 조회에 실패했습니다.", e);
        }
    }

    @Override
    public List<TomorrowConsultationRequestDto> getTomorrowConsultationRequests(Long userNo) {
        try {
            return lawyerDashboardMapper.selectTomorrowConsultationRequests(userNo);
        } catch (Exception e) {
            throw new RuntimeException("내일 상담 신청 목록 조회에 실패했습니다.", e);
        }
    }

    @Override
    public List<TomorrowBroadcastDto> getTomorrowBroadcasts(Long userNo) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
//        log.info("내일 방송 조회 - userNo={}, date={}", userNo, tomorrow);
        return lawyerDashboardMapper.selectTomorrowBroadcasts(tomorrow, userNo);  // userNo 전달
    }

    @Override
    public List<DailyCountDto> getWeeklyConsultations(Long userNo) {
        return lawyerDashboardMapper.selectWeeklyConsultations(userNo);
    }

    @Override
    public List<DailyCountDto> getWeeklyBroadcasts(Long userNo) {
        return lawyerDashboardMapper.selectWeeklyBroadcasts(userNo);
    }


    @Override
    public MonthlyRevenueDto getMonthlyRevenue(Long userNo) {
        try {
            log.info("이달의 수익 조회 시작 - userNo: {}", userNo);

            MonthlyRevenueDto monthlyRevenue = lawyerDashboardMapper.getMonthlyRevenue(userNo);

            if (monthlyRevenue == null) {
                log.warn("이달의 수익 데이터가 없습니다 - userNo: {}", userNo);
                monthlyRevenue = createEmptyRevenueDto();
            }

            // 금액을 만원 단위로 변환해서 프론트엔드에 전달
            String formattedRevenue = formatRevenue(monthlyRevenue.getTotalRevenue());

            log.info("이달의 수익 조회 완료 - userNo: {}, totalRevenue: {}원", userNo, monthlyRevenue.getTotalRevenue());

            return monthlyRevenue;

        } catch (Exception e) {
            log.error("이달의 수익 조회 중 오류 발생 - userNo: {}", userNo, e);
            return createEmptyRevenueDto();
        }
    }

    private MonthlyRevenueDto createEmptyRevenueDto() {
        MonthlyRevenueDto emptyDto = new MonthlyRevenueDto();
        emptyDto.setTotalRevenue(0L);
        emptyDto.setConsultationRevenue(0L);
        emptyDto.setTemplateRevenue(0L);
        emptyDto.setTotalRefundAmount(0L);
        emptyDto.setRevenueMonth(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")));
        return emptyDto;
    }

    private String formatRevenue(Long revenue) {
        if (revenue == null || revenue == 0) return "0만원";
        return String.format("%.0f만원", revenue / 10000.0);
    }
}
