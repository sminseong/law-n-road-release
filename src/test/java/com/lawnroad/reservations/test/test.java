package com.lawnroad.reservations.test;

import com.lawnroad.broadcast.chat.dto.LawyerPreQuestion;
import com.lawnroad.broadcast.chat.dto.PreQuestionDTO;
import com.lawnroad.broadcast.chat.mapper.PreQuestionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class test {

    @Autowired
    private PreQuestionMapper mapper;

    @Test
    void preQuestionMapperTest() {
        PreQuestionDTO dto = mapper.findByPreQuestion(3);
            System.out.println(dto);
    }
    @Test
    void findByPreQuestionLawyerTest() {
        List<LawyerPreQuestion> list = mapper.findByPreQuestionLawyer(3);
        for (LawyerPreQuestion lawyerPreQuestion : list) {
            System.out.println(lawyerPreQuestion);

        }    }


}
