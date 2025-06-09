package com.lawnroad.ReservationsTest;

import com.lawnroad.config.TestConfig;
import com.lawnroad.reservation.mapper.TimeSlotMapper;
import com.lawnroad.reservation.model.TimeSlotVO;
import com.lawnroad.reservation.service.TimeSlotService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TestConfig.class)
class TimeSlotServiceIntegrationTest {

    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private TimeSlotMapper timeSlotMapper;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate; // 필요하다면 직접 쿼리 실행


    @Test
    void generateWeeklyTimeSlots_insertsRowsIntoDatabase() {
        // given
        Long lawyerNo = 1L;
        LocalDate startDate = LocalDate.of(2025, 6, 5);

        // when: 실제 서비스를 호출해서 DB에 삽입
        timeSlotService.generateWeeklyTimeSlots(lawyerNo, startDate);

        // then: 삽입된 레코드를 직접 조회해서 105개가 들어갔는지 확인
        List<TimeSlotVO> slots = sqlSessionTemplate.selectList(
                "com.lawnroad.reservation.mapper.TimeSlotMapper.getByLawyerAndDateRange",
                // 이 파라미터는, 예를 들어 map {lawyerNo:1, startDate:2025-06-05, endDate:2025-06-11}
                // getByLawyerAndDateRange 쿼리는 적절히 mapper XML에 작성해 두어야 합니다.
                Map.of(
                        "lawyerNo", lawyerNo,
                        "startDate", startDate,
                        "endDate", startDate.plusDays(6)
                )
        );
        assertThat(slots.size()).isEqualTo(105);

        // 추가 검증 예시: 첫 레코드, 마지막 레코드 날짜/시간/상태/금액 등
        TimeSlotVO first = slots.get(0);
        assertThat(first.getSlotDate()).isEqualTo(startDate);
        assertThat(first.getSlotTime()).isEqualTo(LocalTime.of(8, 0));
        // …
    }
}
