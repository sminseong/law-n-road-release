package com.lawnroad.keyword.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KeywordAlertMapper {
  
  // 해당 유저의 등록된 키워드 목록 조회
  List<String> selectKeywordsByUserNo(Long userNo);
  
  // 새로운 키워드 등록
  void insertKeyword(@Param("userNo") Long userNo, @Param("keyword") String keyword);
  
  // 키워드 삭제
  void deleteKeyword(@Param("userNo") Long userNo, @Param("keyword") String keyword);
  
  // 특정 키워드가 이미 등록되어 있는지 확인
  boolean existsByUserNoAndKeyword(@Param("userNo") Long userNo, @Param("keyword") String keyword);
}
