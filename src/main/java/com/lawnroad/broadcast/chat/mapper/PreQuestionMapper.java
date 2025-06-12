package com.lawnroad.broadcast.chat.mapper;

import com.lawnroad.broadcast.chat.dto.PreQuestionDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PreQuestionMapper {
    List<PreQuestionDTO> findBySchedule(@Param("scheduleNo") int scheduleNo);


}
