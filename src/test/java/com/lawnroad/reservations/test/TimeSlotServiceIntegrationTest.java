package com.lawnroad.reservations.test;
import com.lawnroad.config.TestConfig;
import com.lawnroad.reservation.service.TimeSlotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;

@SpringBootTest(
        classes = TestConfig.class,
        properties = "spring.sql.init.mode=never"
)
public class TimeSlotServiceIntegrationTest {

    @Autowired
    private TimeSlotService timeSlotService;

    @Test
    @Rollback(false)
    void generateWeeklyTimeSlots_executesInsertOnly() {

        Long lawyerNo   = 1L;
        LocalDate start = LocalDate.of(2025, 6, 9);

        timeSlotService.generateWeeklyTimeSlots(lawyerNo, start);

    }
}
