package com.lawnroad.keyword.service;

import java.util.List;

public interface KeywordAlertService {
  
  // 키워드 전체 조회
  List<String> getKeywords(Long userNo);
  
  // 키워드 등록 (중복 검사 포함)
  void addKeyword(Long userNo, String keyword);
  
  // 키워드 삭제
  void removeKeyword(Long userNo, String keyword);
  
  // 키워드 등록 여부 확인
  boolean exists(Long userNo, String keyword);
}