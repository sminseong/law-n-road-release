package com.lawnroad.reservations.test;

import com.lawnroad.reservation.dto.ReservationsCreateDTO;
import com.lawnroad.reservation.service.ReservationsService;
import com.lawnroad.reservation.mapper.TimeSlotMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
class ReservationConcurrencyTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReservationsService reservationsService;

    @Autowired
    private TimeSlotMapper timeSlotMapper;

    // 테스트에 사용할 슬롯 번호
    private static final Long testSlotNo = 999L;

    @BeforeEach
    void setup() {
        // 1) 이 슬롯에 묶인 이전 예약 데이터 삭제
        jdbcTemplate.update("DELETE FROM reservations WHERE slot_no = ?", testSlotNo);

        // 2) 슬롯 테이블에 해당 PK가 없으면 삽입, 있으면 status만 초기화
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM weekly_time_slots WHERE no = ?", Integer.class, testSlotNo);

        if (count != null && count > 0) {
            jdbcTemplate.update(
                    "UPDATE weekly_time_slots SET status = 1 WHERE no = ?", testSlotNo
            );
        } else {
            // 삽입 시 LocalDate/LocalTime -> java.sql.Date/Time 변환
            jdbcTemplate.update(
                    "INSERT INTO weekly_time_slots (no, user_no, slot_date, slot_time, status, amount) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    testSlotNo,
                    1L,
                    Date.valueOf(LocalDate.now().plusDays(1)),
                    Time.valueOf(LocalTime.of(10, 0)),
                    1,
                    50000L
            );
        }
    }

    @Test
    void concurrentReservation_shouldAllowOnlyOne() throws Exception {
        int threads = 2;
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount  = new AtomicInteger();
        AtomicInteger conflictCount = new AtomicInteger();

        Runnable task = () -> {
            ReservationsCreateDTO dto = new ReservationsCreateDTO();
            dto.setUserNo(1L);
            dto.setSlotNo(testSlotNo);

            try {
                readyLatch.countDown();
                startLatch.await();
                try {
                    reservationsService.createReservation(dto);
                    successCount.incrementAndGet();
                } catch (DataAccessException dae) {
                    // Deadlock or DB exception => treat as conflict
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "이미 예약된 시간입니다. 다른 시간을 선택해 주세요.");
                }
            } catch (ResponseStatusException ex) {
                if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                    conflictCount.incrementAndGet();
                } else {
                    fail("예상치 못한 HTTP 상태: " + ex.getStatusCode());
                }
            } catch (Exception e) {
                fail("예외 발생: " + e.getMessage());
            }
        };

        for (int i = 0; i < threads; i++) {
            new Thread(task).start();
        }

        readyLatch.await();
        startLatch.countDown();

        // 충분히 대기
        Thread.sleep(500);

        assertEquals(1, successCount.get(),  "하나만 예약에 성공해야 합니다.");
        assertEquals(1, conflictCount.get(), "하나는 충돌로 거절되어야 합니다.");
    }
}
