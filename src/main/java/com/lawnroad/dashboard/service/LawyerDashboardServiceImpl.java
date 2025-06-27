package com.lawnroad.dashboard.service;

import com.lawnroad.dashboard.dto.TodayScheduleDto;
import com.lawnroad.dashboard.dto.TomorrowBroadcastDto;
import com.lawnroad.dashboard.dto.TomorrowConsultationRequestDto;
import com.lawnroad.dashboard.dto.WeeklyStatsDto;
import com.lawnroad.dashboard.mapper.LawyerDashboardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LawyerDashboardServiceImpl implements LawyerDashboardService {

    private final LawyerDashboardMapper lawyerDashboardMapper;

    @Override
    public List<TodayScheduleDto> getTodaySchedule(Long lawyerNo) {
//        log.info("오늘 일정 조회 시작 - lawyerNo: {}", lawyerNo);

        try {
            List<TodayScheduleDto> schedules = lawyerDashboardMapper.getTodaySchedule(lawyerNo);

//            log.info("오늘 일정 조회 완료 - lawyerNo: {}, 일정 수: {}", lawyerNo, schedules.size());

            return schedules;

        } catch (Exception e) {
//            log.error("오늘 일정 조회 중 오류 발생 - lawyerNo: {}", lawyerNo, e);
            throw new RuntimeException("오늘 일정 조회에 실패했습니다.", e);
        }
    }

    @Override
    public List<TomorrowConsultationRequestDto> getTomorrowConsultationRequests() {
        try {
//            log.info("내일 상담 신청 목록 조회 시작");
            List<TomorrowConsultationRequestDto> requests = lawyerDashboardMapper.selectTomorrowConsultationRequests();
//            log.info("내일 상담 신청 목록 조회 완료. 총 {}건", requests.size());
            return requests;
        } catch (Exception e) {
//            log.error("내일 상담 신청 목록 조회 중 오류 발생", e);
            throw new RuntimeException("내일 상담 신청 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public List<TomorrowBroadcastDto> getTomorrowBroadcasts(Long userNo) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
//        log.info("내일 방송 조회 - userNo={}, date={}", userNo, tomorrow);
        return lawyerDashboardMapper.selectTomorrowBroadcasts(tomorrow, userNo);  // userNo 전달
    }

    @Override
    public Map<String, int[]> getWeeklyStats(Long lawyerNo) {
//        log.info("변호사 {}의 주간 통계 조회 시작", lawyerNo);

        try {
            List<WeeklyStatsDto> stats = lawyerDashboardMapper.getWeeklyStats(lawyerNo);
            log.info("조회된 통계 데이터: {}", stats);

            // 차트용 배열 생성 (월~일요일 순서로)
            int[] consultations = new int[7]; // 월,화,수,목,금,토,일
            int[] broadcasts = new int[7];

            // 기본값 0으로 초기화
            for (int i = 0; i < 7; i++) {
                consultations[i] = 0;
                broadcasts[i] = 0;
            }

            // 🔥 실제 데이터 매핑 (MySQL DAYOFWEEK를 차트 인덱스로 변환)
            for (WeeklyStatsDto stat : stats) {
                int chartIndex;

                // MySQL DAYOFWEEK를 차트 배열 인덱스로 변환
                // MySQL: 1=일, 2=월, 3=화, 4=수, 5=목, 6=금, 7=토
                // 차트: 0=월, 1=화, 2=수, 3=목, 4=금, 5=토, 6=일
                if (stat.getDayOfWeek() == 1) {
                    chartIndex = 6; // 일요일 -> 인덱스 6
                } else {
                    chartIndex = stat.getDayOfWeek() - 2; // 월요일(2) -> 인덱스 0
                }

                if (chartIndex >= 0 && chartIndex < 7) {
                    consultations[chartIndex] = stat.getConsultationCount();
                    broadcasts[chartIndex] = stat.getBroadcastCount();
//                    log.info("매핑: {}({}) -> 인덱스 {} = 상담:{}, 방송:{}",
//                            stat.getDayName(), stat.getDayOfWeek(), chartIndex,
//                            stat.getConsultationCount(), stat.getBroadcastCount());
                }
            }

            Map<String, int[]> result = new HashMap<>();
            result.put("consultations", consultations);
            result.put("broadcasts", broadcasts);

//            log.info("🔥 최종 차트 데이터 - 상담: {}, 방송: {}",
//                    java.util.Arrays.toString(consultations),
//                    java.util.Arrays.toString(broadcasts));

            return result;

        } catch (Exception e) {
//            log.error("주간 통계 조회 중 오류 발생: ", e);
            Map<String, int[]> defaultData = new HashMap<>();
            defaultData.put("consultations", new int[]{0, 0, 0, 0, 0, 0, 0});
            defaultData.put("broadcasts", new int[]{0, 0, 0, 0, 0, 0, 0});
            return defaultData;
        }
    }
}
