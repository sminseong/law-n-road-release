package com.lawnroad.reservation.scheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lawnroad.reservation.mapper.ReservationsMapper;
import com.lawnroad.reservation.mapper.TimeSlotMapper;
import com.lawnroad.reservation.model.TimeSlotVO;
import com.lawnroad.reservation.service.TimeSlotService;

@Component
public class TimeSlotScheduler {

    private final ReservationsMapper reservationsMapper;
    private final TimeSlotService     timeSlotService;
    private final TimeSlotMapper      timeSlotMapper;

    public TimeSlotScheduler(
            ReservationsMapper reservationsMapper,
            TimeSlotService timeSlotService,
            TimeSlotMapper timeSlotMapper) {
        this.reservationsMapper = reservationsMapper;
        this.timeSlotService    = timeSlotService;
        this.timeSlotMapper     = timeSlotMapper;
    }

    @Scheduled(cron = "0 0 0 * * *")  // 매일 자정 실행
    @Transactional
    public void generateMissingWeeklySlots() {
        LocalDate today   = LocalDate.now();
        LocalDate endDate = today.plusDays(6);

        // 1) distinct user_no (변호사 번호) 조회
        List<Long> lawyerNos = reservationsMapper.selectDistinctLawyerNos();

        for (Long lawyerNo : lawyerNos) {
            // 2) 해당 기간에 생성된 슬롯 조회
            List<TimeSlotVO> existingSlots =
                    timeSlotMapper.getWeeklyTimeSlots(lawyerNo, today, endDate);

            // 3) 존재하는 날짜 집합으로
            Set<LocalDate> existingDates = existingSlots.stream()
                    .map(TimeSlotVO::getSlotDate)
                    .collect(Collectors.toSet());

            // 4) 누락된 날짜만 채우기
            for (int i = 0; i < 7; i++) {
                LocalDate date = today.plusDays(i);
                if (!existingDates.contains(date)) {
                    timeSlotService.generateDailySlots(lawyerNo, date);
                }
            }
        }
    }
}
