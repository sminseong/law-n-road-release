package com.lawnroad.broadcast.chat.service;


import com.lawnroad.broadcast.chat.dto.LawyerPreQuestion;
import com.lawnroad.broadcast.chat.dto.PreQuestionDTO;

import java.util.List;

public interface PreQuestionService {
    PreQuestionDTO findByPreQuestion(int scheduleNo);
    List<LawyerPreQuestion> findByPreQuestionLawyer(int scheduleNo);
    void deletePreQuestion (List<Long> preQuestionNo);

}
