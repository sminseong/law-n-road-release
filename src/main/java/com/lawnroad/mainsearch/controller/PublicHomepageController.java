package com.lawnroad.mainsearch.controller;

import com.lawnroad.mainsearch.dto.LawyerHomepageDto;
import com.lawnroad.mainsearch.service.PublicHomepageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicHomepageController {

private final PublicHomepageService publicHomepageService;

// 변호사 홈화면 데이터 조회
@GetMapping("/homepage/{lawyerNo}")
public ResponseEntity<LawyerHomepageDto> getLawyerHomepage(@PathVariable Long lawyerNo) {
  LawyerHomepageDto dto = publicHomepageService.getLawyerHomepage(lawyerNo);
//  System.out.println(lawyerNo + " : " + dto);
  return ResponseEntity.ok(dto);
}
}