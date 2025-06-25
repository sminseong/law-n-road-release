package com.lawnroad.keyword.controller;

import com.lawnroad.keyword.dto.KeywordRegisterRequestDto;
import com.lawnroad.keyword.service.KeywordAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client/keywords")
@RequiredArgsConstructor
public class KeywordAlertController {
  
  private final KeywordAlertService service;
  private static final Long MOCK_USER_NO = 1L;
  // TODO: 나중에 AuthenticationPrincipal로 대체
  
  // 유저의 키워드 목록 조회
  @GetMapping
  public List<String> getKeywords() {
    return service.getKeywords(MOCK_USER_NO);
  }
  
  // 새로운 키워드 등록
  @PostMapping
  public void addKeyword(@RequestBody KeywordRegisterRequestDto dto) {
    service.addKeyword(MOCK_USER_NO, dto.getKeyword());
  }
  
  // 키워드 삭제
  @DeleteMapping("/{keyword}")
  public void deleteKeyword(@PathVariable String keyword) {
    service.removeKeyword(MOCK_USER_NO, keyword);
  }
  
  // 특정 키워드 등록 여부 확인
  @GetMapping("/exists")
  public boolean exists(@RequestParam String keyword) {
    return service.exists(MOCK_USER_NO, keyword);
  }
}