package com.lawnroad.template.dto;

import lombok.Data;

/**
 * 템플릿 목록 검색 조건 DTO (변호사, 사용자, 공통 조회용)
 */
@Data
public class TemplateSearchConditionDto {
  
  // 현재 페이지 번호 (1부터 시작)
  private int page;
  
  // 페이지당 항목 수
  private int limit;
  
  // 카테고리 번호 (필터링용)
  private Long categoryNo;
  
  // 템플릿 유형 (FILE 또는 EDITOR)
  private String type;
  
  // 템플릿명 검색 키워드
  private String keyword;
  
  // 정렬 기준 (new, popular, priceAsc, priceDesc)
  private String sort;
  
  // Offset 계산
  public int getOffset() {
    return (page - 1) * limit;
  }
}