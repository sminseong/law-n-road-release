package com.lawnroad;

import com.lawnroad.broadcast.live.dto.ScheduleDetailDto;
import com.lawnroad.broadcast.live.mapper.ScheduleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ScheduleMapperIntegrationTest {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Test
    void findByScheduleNo() {
        Long scheduleNo = 17L;
        ScheduleDetailDto dto = scheduleMapper.findByScheduleNo(scheduleNo);
        assertThat(dto).isNotNull();
        assertThat(dto.getKeywords()).isNotEmpty();

        System.out.println(dto);
    }
}
