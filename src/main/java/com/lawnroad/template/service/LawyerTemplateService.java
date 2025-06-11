package com.lawnroad.template.service;

import com.lawnroad.template.dto.LawyerTemplateRegisterDto;
import com.lawnroad.template.dto.TemplateListResponse;
import com.lawnroad.template.dto.TemplateSearchCondition;

/**
 * 변호사 템플릿 등록 서비스 인터페이스
 */
public interface LawyerTemplateService {
  
  /**
   * 템플릿 등록
   * 1. template 테이블에 공통 정보 insert
   * 2. type이 EDITOR면 tmpl_editor_based insert
   * 3. type이 FILE이면 tmpl_file_based insert
   *
   * @param dto 클라이언트에서 전달받은 등록 요청 DTO
   * @param thumbnailPath 썸네일 파일 저장 경로
   */
  void registerTemplate(LawyerTemplateRegisterDto dto, String thumbnailPath);
  
  /**
   * 변호사 본인 템플릿 목록 조회
   *
   * @param lawyerNo   로그인한 변호사 번호
   * @param condition  검색 조건 (페이지, 카테고리, 키워드, 정렬 등)
   * @return           템플릿 목록 + 전체 개수 + 전체 페이지 수
   */
  TemplateListResponse findTemplatesByLawyerNo(Long lawyerNo, TemplateSearchCondition condition);
  
  /**
   * 템플릿 삭제 (PK 기준)
   *
   * @param templateNo 삭제할 템플릿 번호
   */
  void deleteTemplate(Long templateNo);
}