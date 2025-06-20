package com.lawnroad.template.service;

import com.lawnroad.template.dto.*;
import com.lawnroad.template.dto.order.ClientOrderListDto;
import com.lawnroad.template.dto.order.ClientOrderTemplateDto;

import java.util.List;

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
  
  /**
   * 주문 목록을 조회합니다.
   *
   * @param userNo 유저 번호
   * @param status 주문 상태 (ORDERED, PAID, CANCELED) - 선택
   * @param page 페이지 번호 (1부터 시작)
   * @param limit 페이지당 항목 수
   * @return 주문 목록 DTO 리스트
   */
  List<ClientOrderListDto> findOrders(Long userNo,
                                      String keyword,
                                      String status,
                                      int page,
                                      int limit);
  
  /**
   * 주문 목록의 총 개수를 조회합니다. (페이징용)
   *
   * @param userNo 유저 번호
   * @param status 주문 상태 (선택)
   * @return 주문 개수
   */
  int countOrders(Long userNo,
                  String keyword,
                  String status);
  
  /**
   * 특정 주문의 템플릿 목록을 조회합니다.
   *
   * @param orderNo 주문 번호
   * @param type 템플릿 타입 (EDITOR, FILE) - 선택
   * @param categoryNo 카테고리 번호 - 선택
   * @param isDownloaded 다운로드 여부 (0, 1) - 선택
   * @return 템플릿 상세 DTO 리스트
   */
  List<ClientOrderTemplateDto> findTemplatesByOrder(
      Long orderNo, String type, Long categoryNo, Integer isDownloaded, String keyword
  );
  
  /**
   * 특정 주문의 템플릿 개수를 조회합니다. (페이징용)
   *
   * @param orderNo 주문 번호
   * @param type 템플릿 타입 - 선택
   * @param categoryNo 카테고리 번호 - 선택
   * @param isDownloaded 다운로드 여부 - 선택
   * @return 템플릿 개수
   */
  int countTemplatesByOrder(
      Long orderNo, String type, Long categoryNo, Integer isDownloaded, String keyword
  );
  
  /**
   * 에디터 기반 템플릿 상세 조회
   * @param templateNo 조회할 템플릿 번호
   * @return 상세 정보 DTO
   */
  ClientEditorTemplateDetailDto getEditorTemplateDetail(Long templateNo);
  
  /**
   * 파일 기반 템플릿 상세 조회
   * @param templateNo 조회할 템플릿 번호
   * @return 상세 정보 DTO
   */
  ClientFileTemplateDetailDto getFileTemplateDetail(Long templateNo);
  
  // 다운로드 상태 변경
  void markTemplateAsDownloaded(Long orderNo, Long tmplNo);
  
  // 전체 주문 기준
  // 주문 번호 기준으로 다운로드 여부 확인
  boolean checkIsDownloaded(Long orderNo);
  
  // 개별 템플릿 기준
  boolean checkIsDownloaded(Long orderNo, Long tmplNo);
}
