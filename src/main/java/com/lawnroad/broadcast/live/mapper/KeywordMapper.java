package com.lawnroad.broadcast.live.mapper;

import com.lawnroad.broadcast.live.model.KeywordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface KeywordMapper {
    void insertKeyword(KeywordVo keywordVo);
    void deleteByScheduleNo(@Param("scheduleNo") Long scheduleNo);
}
