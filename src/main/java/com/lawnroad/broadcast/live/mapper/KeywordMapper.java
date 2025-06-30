package com.lawnroad.broadcast.live.mapper;

import com.lawnroad.broadcast.live.dto.KeywordAlertMatchDto;
import com.lawnroad.broadcast.live.model.KeywordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KeywordMapper {
    void insertKeyword(KeywordVo keywordVo);
    void deleteByScheduleNo(@Param("scheduleNo") Long scheduleNo);

    // 키워드 알림 등록
    int insertKeywordAlert(@Param("userNo") Long userNo, @Param("keyword") String keyword);

    // 키워드 알림 중복 여부 확인
    boolean existsByUserNoAndKeyword(@Param("userNo") Long userNo, @Param("keyword") String keyword);

    // 키워드 알림 발송 관련
    List<String> findKeywordsByScheduleNo(@Param("scheduleNo") Long scheduleNo);
    String findLawyerNameByScheduleNo(@Param("scheduleNo") Long scheduleNo);
    List<KeywordAlertMatchDto> findUsersByKeywords(@Param("keywords") List<String> keywords);

}
