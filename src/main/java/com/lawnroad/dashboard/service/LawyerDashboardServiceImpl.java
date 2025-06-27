package com.lawnroad.dashboard.service;

import com.lawnroad.dashboard.dto.TodayScheduleDto;
import com.lawnroad.dashboard.dto.TomorrowBroadcastDto;
import com.lawnroad.dashboard.dto.TomorrowConsultationRequestDto;
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
    public List<TodayScheduleDto> getTodaySchedule(Long lawyerNo) {
        log.info("오늘 일정 조회 시작 - lawyerNo: {}", lawyerNo);

        try {
            List<TodayScheduleDto> schedules = lawyerDashboardMapper.getTodaySchedule(lawyerNo);

            log.info("오늘 일정 조회 완료 - lawyerNo: {}, 일정 수: {}", lawyerNo, schedules.size());

            return schedules;

        } catch (Exception e) {
            log.error("오늘 일정 조회 중 오류 발생 - lawyerNo: {}", lawyerNo, e);
            throw new RuntimeException("오늘 일정 조회에 실패했습니다.", e);
        }
    }

    @Override
    public List<TomorrowConsultationRequestDto> getTomorrowConsultationRequests() {
        try {
            log.info("내일 상담 신청 목록 조회 시작");
            List<TomorrowConsultationRequestDto> requests = lawyerDashboardMapper.selectTomorrowConsultationRequests();
            log.info("내일 상담 신청 목록 조회 완료. 총 {}건", requests.size());
            return requests;
        } catch (Exception e) {
            log.error("내일 상담 신청 목록 조회 중 오류 발생", e);
            throw new RuntimeException("내일 상담 신청 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public List<TomorrowBroadcastDto> getTomorrowBroadcasts() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        log.info("내일 방송 조회 - date={}", tomorrow);
        return lawyerDashboardMapper.selectTomorrowBroadcasts(tomorrow);
    }

}
