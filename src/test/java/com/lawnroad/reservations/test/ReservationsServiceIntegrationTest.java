package com.lawnroad.reservations.test;

import com.lawnroad.config.TestConfig;
import com.lawnroad.reservation.dto.ReservationsCreateDTO;
import com.lawnroad.reservation.dto.ReservationsResponseDTO;
import com.lawnroad.reservation.mapper.ReservationsMapper;
import com.lawnroad.reservation.service.ReservationsService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes    = TestConfig.class,
        properties = "spring.sql.init.mode=never"
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReservationsServiceIntegrationTest {

    @Autowired
    private ReservationsService reservationsService;

    @Autowired
    private JdbcTemplate jdbc;

    private static final Long TEST_USER = 6L;
    private static Long slotNo;
    @Autowired
    private ReservationsMapper reservationsMapper;

    @Test
    @Order(1)
    @Rollback(false)
    void createReservation_shouldInsertOrderAndReservation() {
        // --- 1) 초기화 ---
        jdbc.update("DELETE FROM reservations");
        jdbc.update("DELETE FROM orders");
        jdbc.update("DELETE FROM weekly_time_slots");

        // --- 2) 테스트용 슬롯 한 건 미리 삽입 ---
        LocalDate day = LocalDate.of(2025, 6, 9);
        LocalTime time = LocalTime.of(10, 0);
        jdbc.update("""
            INSERT INTO weekly_time_slots
              (user_no, slot_date, slot_time, status, amount)
            VALUES (?, ?, ?, 0, ?)
            """,
                TEST_USER, day, time, 30000L
        );
        slotNo = jdbc.queryForObject(
                "SELECT no FROM weekly_time_slots WHERE user_no=? AND slot_date=? AND slot_time=?",
                Long.class, TEST_USER, day, time
        );
        assertNotNull(slotNo);

        // --- 3) 서비스 호출 (주문 생성 + 예약 삽입) ---
        ReservationsCreateDTO dto = new ReservationsCreateDTO();
        dto.setUserNo(TEST_USER);
        dto.setSlotNo(slotNo);
        dto.setContent("서비스 레이어 단위 테스트 예약");
        reservationsService.createReservation(dto);

        // --- 4) orders 테이블 검증 ---
        Long orderCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE user_no=?",
                Long.class, TEST_USER
        );
        assertEquals(1L, orderCount);

        // --- 5) reservations 테이블 검증 ---
        Long resCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM reservations WHERE user_no=? AND slot_no=?",
                Long.class, TEST_USER, slotNo
        );
        assertEquals(1L, resCount);
    }

    @Test
    @Order(2)
    void getReservationsByUser_shouldReturnReservation() {
        // --- 서비스 조회 ---
        List<ReservationsResponseDTO> list =
                reservationsService.getReservationsByUser(TEST_USER);

        // --- 터미널에 출력 ---
        System.out.println("▶▶ Reservations for user " + TEST_USER + ": " + list);

        // --- 최소 1건은 나와야 함 ---
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void changeStatus_shouldChangeStatus() {
        reservationsMapper.updateReservationStatus(3L,"CANCELED");
    }
}
