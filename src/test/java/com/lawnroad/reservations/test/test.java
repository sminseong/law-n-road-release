package com.lawnroad.reservations.test;

import com.lawnroad.broadcast.chat.dto.PreQuestionDTO;
import com.lawnroad.broadcast.chat.mapper.PreQuestionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class test {



    @Autowired
    private PreQuestionMapper mapper;

    @Test
    void preQuestionMapperTest() {
        PreQuestionDTO dto = mapper.findBySchedule(3);
        System.out.println(dto);
    }

}
