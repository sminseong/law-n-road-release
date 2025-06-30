package com.lawnroad.reservations.test;

import com.lawnroad.broadcast.chat.dto.AutoReplyDTO;
import com.lawnroad.broadcast.chat.dto.LawyerPreQuestion;
import com.lawnroad.broadcast.chat.dto.PreQuestionDTO;
import com.lawnroad.broadcast.chat.dto.PreQuestionItem;
import com.lawnroad.broadcast.chat.mapper.AutoReplyMapper;
import com.lawnroad.broadcast.chat.mapper.PreQuestionMapper;
import com.lawnroad.broadcast.live.mapper.BroadcastMapper;
import com.lawnroad.broadcast.live.mapper.VodMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ChatTest {

    @Autowired
    private PreQuestionMapper mapper;
    @Autowired
    private AutoReplyMapper autoReplyMapper;

    @Autowired
    private PreQuestionMapper preQuestionMapper;
    @Autowired
    private BroadcastMapper broadcastMapper;

    @Autowired
    private VodMapper vodMapper;

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

        }
    }

    @Test
    void insertAutoReplyLawyerTest() {
        AutoReplyDTO dto = new AutoReplyDTO();
        dto.setScheduleNo(3L);
        dto.setKeyword("hello2");
        dto.setMessage("자동응답 메시지2");
        autoReplyMapper.insertAutoReply(dto);
    }

//    @Test
//    void findByAutoReplyLawyerTest() {
//        List<AutoReplyDTO> list = autoReplyMapper.findByAutoReply(3L);
//        for (AutoReplyDTO autoReplyDTO : list) {
//            System.out.println(autoReplyDTO);
//
//        }
//    }


    @Test
    void insertPreQuestionTest() {
        PreQuestionItem dto = new PreQuestionItem();
        dto.setScheduleNo(3L);
        dto.setPreQuestionContent("hello2");
        dto.setNickname("닉네임3");
        preQuestionMapper.insertPreQuestion(dto);
    }


    @Test
    void deletePreQuestionByUserTest() {
        Long scheduleNo = 3L;
        Long questionNo = 31L;
        Long userNo = 14L;
       preQuestionMapper.deletePreQuestionByUser(scheduleNo, questionNo, userNo);

    }

    @Test
    @DisplayName("findReplyMessage - broadcastNo와 keyword로 자동응답 메시지 조회")
    void testFindReplyMessage() {
        // given: 테스트용 broadcastNo와 keyword
        Long broadcastNo = 53L;
        String keyword = "예약";

        // when
        String result = autoReplyMapper.findReplyMessage(broadcastNo, keyword);

        // then
        assertThat(result).isNotNull();
        System.out.println("자동응답 메시지 결과: " + result);
    }


    @Test
    void findByBroadcastPreQuestionTest() {
        Long broadcastNo = 53L;

        List<LawyerPreQuestion> questions = preQuestionMapper.findByBroadcastPreQuestion(broadcastNo);

            for (LawyerPreQuestion q : questions) {
                System.out.println(q);
            }

    }

    @Test
    void selectNameByUserNo() {
        Long userNo = 80L;

        String name = vodMapper.selectNameByUserNo(userNo);

        System.out.println("Lawyer Name: " + name);


    }

}
