package com.lawnroad.template.dto;

import lombok.Data;

import java.util.List;

@Data
public class TemplateListResponseDto {
  // 조회된 전체 템플릿 개수
  private int totalCount;
  // 선택된 limit, offset 으로 계산되는 총 페이지수
  private int totalPages;
  // 조회된 실제 템플릿 데이터
  private List<TemplateDto> templates;
}