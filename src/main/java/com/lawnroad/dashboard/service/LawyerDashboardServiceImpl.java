package com.lawnroad.dashboard.service;

import com.lawnroad.dashboard.dto.*;
import com.lawnroad.dashboard.mapper.LawyerDashboardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LawyerDashboardServiceImpl implements LawyerDashboardService {

    private final LawyerDashboardMapper lawyerDashboardMapper;

    @Override
    public List<TodayScheduleDto> getTodaySchedule(Long userNo) {
        return lawyerDashboardMapper.getTodaySchedule(userNo);
    }

    @Override
    public List<TomorrowConsultationRequestDto> getTomorrowConsultationRequests(Long userNo) {
        return lawyerDashboardMapper.selectTomorrowConsultationRequests(userNo);
    }

    @Override
    public List<TomorrowBroadcastDto> getTomorrowBroadcasts(Long userNo) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
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
        return lawyerDashboardMapper.getMonthlyRevenue(userNo);
    }

    @Override
    public MonthlyTemplateSalesDto getMonthlyTemplateSales(Long userNo) {
        return lawyerDashboardMapper.getMonthlyTemplateSales(userNo);
    }


}
