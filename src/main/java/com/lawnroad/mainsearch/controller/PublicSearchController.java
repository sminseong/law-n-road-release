package com.lawnroad.mainsearch.controller;

import com.lawnroad.mainsearch.dto.SearchResponseDto;
import com.lawnroad.mainsearch.service.PublicSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/search")
@RequiredArgsConstructor
public class PublicSearchController {
  
  private final PublicSearchService service;
  
  @GetMapping
  public SearchResponseDto search(
      @RequestParam("q") String keyword,
      @RequestParam(value = "category",    required = false) Long category,
      @RequestParam(value = "onlyLawyers", defaultValue = "false") boolean onlyLawyers
  ) {
//    System.out.printf("search() called → q=%s, category=%s, onlyLawyers=%s%n",
//        keyword, category, onlyLawyers);
    
    if (onlyLawyers) {
      // 추후에 검색·페이징 로직이 필요하면 service.searchOnlyLawyers(...) 구현
      // return service.searchOnlyLawyers(keyword, category);
      return null;
    } else {
      // 기존 요약 검색 (변호사 10, 템플릿 20, QnA 10)
      return service.search(keyword, category);
    }
  }
}
