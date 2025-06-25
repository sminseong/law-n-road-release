package com.lawnroad.keyword.service;

import com.lawnroad.keyword.mapper.KeywordAlertMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordAlertServiceImpl implements KeywordAlertService {
  
  private final KeywordAlertMapper mapper;
  
  // 유저의 키워드 전체 조회
  @Override
  public List<String> getKeywords(Long userNo) {
    return mapper.selectKeywordsByUserNo(userNo);
  }
  
  // 키워드 중복 확인 후 등록
  @Override
  public void addKeyword(Long userNo, String keyword) {
    if (mapper.existsByUserNoAndKeyword(userNo, keyword)) {
      throw new IllegalArgumentException("이미 등록된 키워드입니다.");
    }
    mapper.insertKeyword(userNo, keyword);
  }
  
  // 키워드 삭제
  @Override
  public void removeKeyword(Long userNo, String keyword) {
    mapper.deleteKeyword(userNo, keyword);
  }
  
  // 특정 키워드 등록 여부 확인
  @Override
  public boolean exists(Long userNo, String keyword) {
    return mapper.existsByUserNoAndKeyword(userNo, keyword);
  }
}
