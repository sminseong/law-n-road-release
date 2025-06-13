package com.lawnroad.orders.test;

import com.lawnroad.config.TestConfig;
import com.lawnroad.payment.dto.OrdersStatusUpdateDTO;
import com.lawnroad.payment.dto.OrdersCreateDTO;
import com.lawnroad.payment.model.OrdersVO;
import com.lawnroad.payment.service.OrdersService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = TestConfig.class,
        properties = "spring.sql.init.mode=never"
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdersServiceIntegrationTest {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private JdbcTemplate jdbc;

    private static Long createdOrderNo;
    private static final Long TEST_USER = 6L;

    @Test
    @Order(1)
    @Rollback(false)
    void createOrder_shouldInsertAndReturnOrderNo() {
        // 초기화
        jdbc.update("DELETE FROM orders");

        // DTO 준비
        OrdersCreateDTO dto = new OrdersCreateDTO();
        dto.setUserNo(TEST_USER);
        dto.setTotalAmount(15000L);
        dto.setStatus("ORDERED");
        dto.setOrderType("RESERVATION");

        // 서비스 호출
        createdOrderNo = ordersService.createOrder(dto);

        // DTO에 no 채워졌는지
        System.out.println("Created Order No: " + dto.getNo());
        assertNotNull(dto.getNo());
        assertEquals(createdOrderNo, dto.getNo());

        // DB 검증
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE no=? AND user_no=? AND total_amount=? AND status=? AND order_type=?",
                Integer.class,
                createdOrderNo, TEST_USER, 15000L, "ORDERED", "RESERVATION"
        );
        assertEquals(1, count);
    }

    @Test
    @Order(2)
    void getOrder_shouldReturnCorrectVO() {
        // 조회
        OrdersVO vo = ordersService.getOrder(createdOrderNo);

        // 출력
        System.out.println("▶▶ Retrieved Order: " + vo);

        // 검증
        assertEquals(createdOrderNo, vo.getNo());
        assertEquals(TEST_USER, vo.getUserNo());
        assertEquals(15000L, vo.getTotalAmount());
        assertEquals("ORDERED", vo.getStatus());
        assertEquals("RESERVATION", vo.getOrderType());
        assertTrue(vo.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertEquals(vo.getCreatedAt(), vo.getUpdatedAt());
    }

    @Test
    @Order(3)
    void changeStatus_shouldUpdateStatusAndUpdatedAt() throws InterruptedException {
        // 기존 updated_at
        LocalDateTime before = jdbc.queryForObject(
                "SELECT updated_at FROM orders WHERE no = ?",
                LocalDateTime.class,
                createdOrderNo
        );
        assertNotNull(before);

        // 대기
        Thread.sleep(1100);

        // 상태 변경
        OrdersStatusUpdateDTO updateDto = new OrdersStatusUpdateDTO();
        updateDto.setOrderNo(createdOrderNo);
        updateDto.setStatus("PAID");
        ordersService.changeStatus(updateDto);

        // DB 상태 확인
        String dbStatus = jdbc.queryForObject(
                "SELECT status FROM orders WHERE no = ?",
                String.class,
                createdOrderNo
        );
        assertEquals("PAID", dbStatus);

        // updated_at 확인
        LocalDateTime after = jdbc.queryForObject(
                "SELECT updated_at FROM orders WHERE no = ?",
                LocalDateTime.class,
                createdOrderNo
        );
        assertTrue(after.isAfter(before));
    }
}
