package com.lawnroad.reservations.test;

import com.lawnroad.config.TestConfig;
import com.lawnroad.reservation.dto.TimeSlotResponseDTO;
import com.lawnroad.reservation.model.TimeSlotVO;
import com.lawnroad.reservation.service.TimeSlotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

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
        Long lawyerNo = 1L;
        LocalDate start = LocalDate.of(2025, 6, 16);
        timeSlotService.generateWeeklyTimeSlots(lawyerNo, start);
    }

    // 주간슬롯 조회 테스트 → DTO 기반으로 변경
    @Test
    void getWeeklyTimeSlots() {
        Long userNo = 1L;
        LocalDate startDate = LocalDate.of(2025, 6, 9);

        List<TimeSlotResponseDTO> dtos = timeSlotService.getWeeklyTimeSlots(userNo, startDate);
        assertThat(dtos).isNotEmpty();

        dtos.forEach(dto -> System.out.println(
                dto.getNo()
                        + " | " + dto.getSlotDate()
                        + " | " + dto.getSlotTime()
                        + " | status=" + dto.getStatus()
                        + " | amount=" + dto.getAmount()
        ));
    }

    // 단일 슬롯 상태 변경 테스트 → DTO → VO 변환 후 update 호출
    @Test
    void updateWeeklyTimeSlotsSingle() {
        Long userNo = 1L;
        LocalDate weekStart = LocalDate.of(2025, 6, 13);

        // 1) DTO 조회
        List<TimeSlotResponseDTO> dtos = timeSlotService.getWeeklyTimeSlots(userNo, weekStart);
        assertThat(dtos).isNotEmpty();

        // 2) DTO → VO 변환
        TimeSlotResponseDTO firstDto = dtos.get(0);
        TimeSlotVO updated = new TimeSlotVO(
                382L,
                userNo,
                firstDto.getSlotDate(),
                firstDto.getSlotTime(),
                0,                        // 예약됨
                firstDto.getAmount()
        );

        // 3) 상태 업데이트
        timeSlotService.updateSlotStatuses(List.of(updated));
    }

    // 복수 슬롯 상태 변경 테스트
    @Test
    void updateWeeklyTimeSlots() {
        Long userNo = 1L;
        LocalDate weekStart = LocalDate.of(2025, 6, 9);

        List<TimeSlotResponseDTO> dtos = timeSlotService.getWeeklyTimeSlots(userNo, weekStart);
        assertThat(dtos.size()).isGreaterThanOrEqualTo(3);

        List<TimeSlotVO> toDisable = dtos.subList(0, 3).stream()
                .map(dto -> new TimeSlotVO(
                        dto.getNo(),
                        userNo,
                        dto.getSlotDate(),
                        dto.getSlotTime(),
                        1,
                        dto.getAmount()
                ))
                .collect(Collectors.toList());

        timeSlotService.updateSlotStatuses(toDisable);
    }
}
