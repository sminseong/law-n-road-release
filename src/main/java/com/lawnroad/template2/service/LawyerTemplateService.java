package com.lawnroad.template2.service;

import com.lawnroad.template2.dto.LawyerTemplateRegisterDto;

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
   * @param filePath 템플릿 파일 저장 경로 (파일 기반일 때만 사용)
   */
  void registerTemplate(LawyerTemplateRegisterDto dto, String thumbnailPath, String filePath);
}