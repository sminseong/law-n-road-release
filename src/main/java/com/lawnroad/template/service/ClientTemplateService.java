package com.lawnroad.template.service;

import com.lawnroad.template.dto.ClientTemplateDetailResponseDto;
import com.lawnroad.template.dto.TemplateListResponseDto;
import com.lawnroad.template.dto.TemplateSearchConditionDto;

public interface ClientTemplateService {
  /**
   * 전체 템플릿 목록 조회 (사용자용)
   * @param condition 조회 조건
   * @return 템플릿 목록 + 전체 개수 + 전체 페이지 수
   */
  TemplateListResponseDto findAllTemplates(TemplateSearchConditionDto condition);
  
  /**
   * 템플릿 상세 정보 조회 (사용자용)
   * @param templateNo 템플릿 고유번호
   * @return 템플릿 상세 응답 DTO
   */
  ClientTemplateDetailResponseDto findTemplateDetailByNo(Long templateNo);
}
