package com.lawnroad.keyword.controller;

import com.lawnroad.common.util.JwtTokenUtil;
import com.lawnroad.keyword.dto.KeywordRegisterRequestDto;
import com.lawnroad.keyword.service.KeywordAlertService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client/keywords")
@RequiredArgsConstructor
public class KeywordAlertController {
  
  private final KeywordAlertService service;
  private final JwtTokenUtil jwtUtil;
  
  // 유저의 키워드 목록 조회
  @GetMapping
  public List<String> getKeywords(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    Claims claims = jwtUtil.parseToken(token);
    long userNo = claims.get("no", Long.class);
    
    return service.getKeywords(userNo);
  }
  
  // 새로운 키워드 등록
  @PostMapping
  public void addKeyword(@RequestHeader("Authorization") String authHeader, @RequestBody KeywordRegisterRequestDto dto) {
    String token = authHeader.replace("Bearer ", "");
    Claims claims = jwtUtil.parseToken(token);
    long userNo = claims.get("no", Long.class);
    
    service.addKeyword(userNo, dto.getKeyword());
  }
  
  // 키워드 삭제
  @DeleteMapping("/{keyword}")
  public void deleteKeyword(@RequestHeader("Authorization") String authHeader, @PathVariable String keyword) {
    String token = authHeader.replace("Bearer ", "");
    Claims claims = jwtUtil.parseToken(token);
    long userNo = claims.get("no", Long.class);
    
    service.removeKeyword(userNo, keyword);
  }
  
  // 특정 키워드 등록 여부 확인
  @GetMapping("/exists")
  public boolean exists(@RequestHeader("Authorization") String authHeader, @RequestParam String keyword) {
    String token = authHeader.replace("Bearer ", "");
    Claims claims = jwtUtil.parseToken(token);
    long userNo = claims.get("no", Long.class);
    
    return service.exists(userNo, keyword);
  }
}