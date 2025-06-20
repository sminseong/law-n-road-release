package com.lawnroad.mainsearch.controller;


import com.lawnroad.mainsearch.service.LawyerService;
import com.lawnroad.mainsearch.dto.LawyerBasicInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lawyer")
@RequiredArgsConstructor
public class LawyerController {
  
  private final LawyerService lawyerService;
  
  // 변호사 기본 정보 조회
  @GetMapping("/info/{lawyerNo}")
  public ResponseEntity<LawyerBasicInfoDto> getLawyerBasicInfo(@PathVariable Long lawyerNo) {
    LawyerBasicInfoDto dto = lawyerService.findLawyerBasicInfoByNo(lawyerNo);
    return ResponseEntity.ok(dto);
  }
}
