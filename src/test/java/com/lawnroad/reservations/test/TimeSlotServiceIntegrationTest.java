package com.lawnroad.reservations.test;

import com.lawnroad.config.TestConfig;
import com.lawnroad.reservation.model.TimeSlotVO;
import com.lawnroad.reservation.service.TimeSlotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.assertThat;


import java.time.LocalDate;
import java.util.List;

@SpringBootTest(
        classes = TestConfig.class,
        properties = "spring.sql.init.mode=never"
)
public class TimeSlotServiceIntegrationTest {

    @Autowired
    private TimeSlotService timeSlotService;

    // 일주일 치 주간 슬롯 생성 테스트
    @Test
    @Rollback(false)
    void createWeeklyTimeSlots() {

        Long lawyerNo   = 1L;
        LocalDate start = LocalDate.of(2025, 6, 9);

        timeSlotService.generateWeeklyTimeSlots(lawyerNo, start);

    }

    // 주간슬롯 조회 테스트
    @Test
    void getWeeklyTimeSlots() {
        Long userNo = 1L;
        LocalDate startDate = LocalDate.of(2025, 6, 9);

        List<TimeSlotVO> slots = timeSlotService.getWeeklyTimeSlots(userNo, startDate);
        slots.forEach(vo -> System.out.println(
                vo.getUserNo()
                        + " | " + vo.getSlotDate()
                        + " | " + vo.getSlotTime()
                        + " | status=" + vo.getStatus()
                        + " | amount=" + vo.getAmount()
        ));
    }

    @Test
    void updateWeeklyTimeSlotsSingle() {
        Long userNo   = 1L;
        LocalDate weekStart = LocalDate.of(2025, 6, 9);

        List<TimeSlotVO> slots = timeSlotService.getWeeklyTimeSlots(userNo, weekStart);
        assertThat(slots).isNotEmpty();

        TimeSlotVO first = slots.get(0);
        TimeSlotVO updated = new TimeSlotVO(
                first.getNo(),
                first.getUserNo(),
                first.getSlotDate(),
                first.getSlotTime(),
                1,
                first.getAmount()
        );
        timeSlotService.updateSlotStatuses(List.of(updated));
    }

    @Test
    void updateWeeklyTimeSlots() {
        Long userNo   = 1L;
        LocalDate weekStart = LocalDate.of(2025, 6, 9);

        List<TimeSlotVO> slots = timeSlotService.getWeeklyTimeSlots(userNo, weekStart);
        assertThat(slots.size()).isGreaterThanOrEqualTo(3);

        List<TimeSlotVO> toDisable = slots.subList(0, 3).stream()
                .map(vo -> new TimeSlotVO(
                        vo.getNo(),
                        vo.getUserNo(),
                        vo.getSlotDate(),
                        vo.getSlotTime(),
                        1,
                        vo.getAmount()
                ))
                .toList();

        timeSlotService.updateSlotStatuses(toDisable);
    }
}
