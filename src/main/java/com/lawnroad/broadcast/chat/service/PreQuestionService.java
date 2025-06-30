package com.lawnroad.broadcast.chat.service;


import com.lawnroad.broadcast.chat.dto.LawyerPreQuestion;
import com.lawnroad.broadcast.chat.dto.PreQuestionDTO;
import com.lawnroad.broadcast.chat.dto.PreQuestionItem;

import java.util.List;

public interface PreQuestionService {
    void insertPreQuestion(PreQuestionItem preQuestionItem);

    PreQuestionDTO findByPreQuestion(int scheduleNo);

    List<LawyerPreQuestion> findByPreQuestionLawyer(int scheduleNo);

    void deleteLawyerPreQuestion(List<Long> preQuestionNo);

    void deletePreQuestionByUser(Long scheduleNo, Long questionNo, Long userNo);
    List<LawyerPreQuestion> findByBroadcastPreQuestion(Long broadcastNo);

}