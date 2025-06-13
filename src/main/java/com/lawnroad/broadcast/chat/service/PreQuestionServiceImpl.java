package com.lawnroad.broadcast.chat.service;

import com.lawnroad.broadcast.chat.dto.LawyerPreQuestion;
import com.lawnroad.broadcast.chat.dto.PreQuestionDTO;
import com.lawnroad.broadcast.chat.mapper.PreQuestionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreQuestionServiceImpl implements PreQuestionService {

    private final PreQuestionMapper preQuestionMapper;


    @Override
    public PreQuestionDTO findByPreQuestion(int scheduleNo) {
        return preQuestionMapper.findByPreQuestion(scheduleNo);
    }

    @Override
    public List<LawyerPreQuestion> findByPreQuestionLawyer(int scheduleNo) {
        return preQuestionMapper.findByPreQuestionLawyer(scheduleNo);
    }


    @Override
   public void deletePreQuestion (List<Long> preQuestionNo) {
         preQuestionMapper.deletePreQuestion(preQuestionNo);
   }


}
