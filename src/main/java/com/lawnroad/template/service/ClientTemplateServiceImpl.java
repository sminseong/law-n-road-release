package com.lawnroad.template.service;

import com.lawnroad.template.dto.*;
import com.lawnroad.template.dto.order.ClientOrderListDto;
import com.lawnroad.template.dto.order.ClientOrderTemplateDto;
import com.lawnroad.template.mapper.ClientTemplateMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientTemplateServiceImpl implements ClientTemplateService {
  private final ClientTemplateMapper clientTemplateMapper;
  
  /**
   * 전체 템플릿 목록 조회 (사용자용)
   * - 검색어, 카테고리, 정렬, 유형 등 조건을 기반으로 템플릿 목록을 조회함
   * - limit/offset 기반 페이징 처리
   * - 총 템플릿 수 및 총 페이지 수 포함
   */
  @Override
  public TemplateListResponseDto findAllTemplates(TemplateSearchConditionDto condition) {
    List<TemplateDto> list = clientTemplateMapper.selectAllTemplates(
        condition.getCategoryNo(),
        condition.getKeyword(),
        condition.getType(),   // type으로 통일 ('EDITOR', 'FILE', 'free', 'special')
        condition.getSort(),
        condition.getLimit(),
        condition.getOffset()
    );
    
    int totalCount = clientTemplateMapper.countAllTemplates(
        condition.getCategoryNo(),
        condition.getKeyword(),
        condition.getType()
    );
    
    int totalPages = (int) Math.ceil((double) totalCount / condition.getLimit());
    
    TemplateListResponseDto result = new TemplateListResponseDto();
    result.setTemplates(list);
    result.setTotalCount(totalCount);
    result.setTotalPages(totalPages);
    
    return result;
  }
  
  /**
   * 템플릿 상세 정보 조회 (사용자용)
   * @param templateNo 템플릿 고유번호
   * @return 템플릿 상세 응답 DTO
   */
  @Override
  public ClientTemplateDetailResponseDto findTemplateDetailByNo(Long templateNo) {
    return clientTemplateMapper.selectTemplateDetailByNo(templateNo);
  }
  
  
  /**
   * 주문 목록을 조회합니다.
   *
   * @param userNo 유저 번호
   * @param status 주문 상태 (ORDERED, PAID, CANCELED) - 선택
   * @param page 페이지 번호 (1부터 시작)
   * @param limit 페이지당 항목 수
   * @return 주문 목록 DTO 리스트
   */
  @Override
  public List<ClientOrderListDto> findOrders(
      Long userNo,
      String keyword,
      String status,
      int page,
      int limit
  ) {
    int offset = (page - 1) * limit;
    // ↕️ 여기서 반드시 status 먼저, 그 다음 keyword
    return clientTemplateMapper.selectOrdersByUserNo(
        userNo,    // @Param("userNo")
        status,    // @Param("status")
        keyword,   // @Param("keyword")
        limit,     // @Param("limit")
        offset     // @Param("offset")
    );
  }
  
  /**
   * 주문 목록의 총 개수를 조회합니다.
   *
   * @param userNo 유저 번호
   * @param status 주문 상태 (선택)
   * @return 주문 개수
   */
  @Override
  public int countOrders(
      Long userNo,
      String keyword,
      String status
  ) {
    // ↕️ Mapper 순서(userNo, keyword, status)와 동일
    return clientTemplateMapper.countOrdersByUserNo(
        userNo,    // @Param("userNo")
        keyword,   // @Param("keyword")
        status     // @Param("status")
    );
  }
  
  /**
   * 특정 주문의 템플릿 목록을 조회합니다.
   *
   * @param orderNo 주문 번호
   * @param type 템플릿 타입 (EDITOR, FILE) - 선택
   * @param categoryNo 카테고리 번호 - 선택
   * @param isDownloaded 다운로드 여부 (0, 1) - 선택
   * @return 템플릿 상세 DTO 리스트
   */
  @Override
  public List<ClientOrderTemplateDto> findTemplatesByOrder(Long orderNo, String type, Long categoryNo, Integer isDownloaded) {
    return clientTemplateMapper.selectTemplatesByOrderNo(orderNo, type, categoryNo, isDownloaded);
  }
  
  /**
   * 특정 주문의 템플릿 개수를 조회합니다. (페이징용)
   *
   * @param orderNo 주문 번호
   * @param type 템플릿 타입 - 선택
   * @param categoryNo 카테고리 번호 - 선택
   * @param isDownloaded 다운로드 여부 - 선택
   * @return 템플릿 개수
   */
  @Override
  public int countTemplatesByOrder(Long orderNo, String type, Long categoryNo, Integer isDownloaded) {
    return clientTemplateMapper.countTemplatesByOrderNo(orderNo, type, categoryNo, isDownloaded);
  }
  
  // 에디터 기반 템플릿 상세 조회
  @Override
  @Transactional(readOnly = true)
  public ClientEditorTemplateDetailDto getEditorTemplateDetail(Long templateNo) {
    return clientTemplateMapper.findEditorTemplateDetail(templateNo);
  }
  
  // 파일 기반 템플릿 상세 조회
  @Override
  @Transactional(readOnly = true)
  public ClientFileTemplateDetailDto getFileTemplateDetail(Long templateNo) {
    return clientTemplateMapper.findFileTemplateDetail(templateNo);
  }
  
  // 다운로드 상태로 변경
  @Override
  public void markTemplateAsDownloaded(Long orderNo, Long tmplNo) {
    clientTemplateMapper.updateTemplateDownloaded(orderNo, tmplNo);
  }
  
  // 다운로드 상태 조회
  @Override
  public boolean checkIsDownloaded(Long orderNo) {
    return clientTemplateMapper.selectIsDownloadedByOrderNo(orderNo);
  }
  
  // 다운로드 상태 조회 (개별 조회)
  @Override
  public boolean checkIsDownloaded(Long orderNo, Long tmplNo) {
    return clientTemplateMapper.selectIsDownloadedByOrderNoAndTmplNo(orderNo, tmplNo);
  }
}
