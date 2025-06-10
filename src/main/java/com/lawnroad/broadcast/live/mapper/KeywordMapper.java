package com.lawnroad.broadcast.live.mapper;

import com.lawnroad.broadcast.live.model.KeywordVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KeywordMapper {
    void insertKeyword(KeywordVo keywordVo);
}
