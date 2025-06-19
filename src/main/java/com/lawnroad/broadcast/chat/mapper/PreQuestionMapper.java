package com.lawnroad.broadcast.chat.mapper;

import com.lawnroad.broadcast.chat.dto.LawyerPreQuestion;
import com.lawnroad.broadcast.chat.dto.PreQuestionDTO;
import com.lawnroad.broadcast.chat.dto.PreQuestionItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PreQuestionMapper {
    void insertPreQuestion(PreQuestionItem preQuestionItem);
    PreQuestionDTO findByPreQuestion(@Param("scheduleNo") int scheduleNo);
    List<LawyerPreQuestion> findByPreQuestionLawyer(@Param("scheduleNo") int scheduleNo);
    void deleteLawyerPreQuestion(@Param("list") List<Long> noList);
    void deletePreQuestionByUser(Long scheduleNo, Long questionNo, Long userNo);
    List<LawyerPreQuestion> findByBroadcastPreQuestion(@Param("broadcastNo") Long broadcastNo);

}
