package com.lawnroad.template.service;

import com.lawnroad.template.dto.*;

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
  TemplateListResponseDto findTemplatesByLawyerNo(Long lawyerNo, TemplateSearchConditionDto condition);
  
  /**
   * 템플릿 삭제 (PK 기준)
   *
   * @param templateNo 삭제할 템플릿 번호
   */
  void deleteTemplate(Long templateNo);
  
  /**
   * 에디터 기반 템플릿 상세 조회
   * @param templateNo 조회할 템플릿 번호
   * @return 상세 정보 DTO
   */
  EditorTemplateDetailDto getEditorTemplateDetail(Long templateNo);
  
  /**
   * 파일 기반 템플릿 상세 조회
   * @param templateNo 조회할 템플릿 번호
   * @return 상세 정보 DTO
   */
  FileTemplateDetailDto getFileTemplateDetail(Long templateNo);
  
  /**
   * 기존 템플릿 수정 (복제 후 삭제 방식)
   * 1. 기존 템플릿 정보를 조회
   * 2. 새로운 템플릿으로 복제 (생성일 유지, 판매수 초기화)
   * 3. 서브 테이블(tmpl_editor_based / tmpl_file_based)도 복제
   * 4. 기존 템플릿은 소프트 딜리트 처리
   *
   * @param dto 수정 요청 DTO
   * @param thumbnailPath 썸네일 경로 (파일 저장 후 전달)
   */
  void updateTemplateByClone(LawyerTemplateUpdateDto dto, String thumbnailPath);
}