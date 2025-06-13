package com.lawnroad.broadcast.chat.mapper;

import com.lawnroad.broadcast.chat.dto.LawyerPreQuestion;
import com.lawnroad.broadcast.chat.dto.PreQuestionDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PreQuestionMapper {
    PreQuestionDTO findByPreQuestion(@Param("scheduleNo") int scheduleNo);
    List<LawyerPreQuestion> findByPreQuestionLawyer(@Param("scheduleNo") int scheduleNo);
    void deletePreQuestion(@Param("list") List<Long> noList);


}
