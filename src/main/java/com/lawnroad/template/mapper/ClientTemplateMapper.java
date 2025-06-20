package com.lawnroad.template.mapper;

import com.lawnroad.template.dto.*;
import com.lawnroad.template.dto.order.ClientOrderListDto;
import com.lawnroad.template.dto.order.ClientOrderTemplateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClientTemplateMapper {
  
  /**
   * 전체 템플릿 목록 조회 (페이징 + 필터)
   */
  List<TemplateDto> selectAllTemplates(
      @Param("categoryNo") Long categoryNo,
      @Param("keyword") String keyword,
      @Param("tag") String tag,
      @Param("sort") String sort,
      @Param("limit") int limit,
      @Param("offset") int offset
  );
  
  /**
   * 전체 템플릿 개수 조회 (페이징용)
   */
  int countAllTemplates(
      @Param("categoryNo") Long categoryNo,
      @Param("keyword") String keyword,
      @Param("tag") String tag
  );
  
  /**
   * 제품 상세조회
   * @param templateNo 템플릿 고유번호
   * @return 상세 DTO
   */
  ClientTemplateDetailResponseDto selectTemplateDetailByNo(
      @Param("templateNo") Long templateNo
  );
  
  // 주문 목록 조회
  List<ClientOrderListDto> selectOrdersByUserNo(
      @Param("userNo")   Long   userNo,
      @Param("status")   String status,
      @Param("keyword")  String keyword,
      @Param("limit")    int    limit,
      @Param("offset")   int    offset
  );
  
  // 주문 개수 조회
  int countOrdersByUserNo(
      @Param("userNo")   Long   userNo,
      @Param("keyword")  String keyword,
      @Param("status")   String status
  );
  
  // 주문 상세 템플릿 목록
  List<ClientOrderTemplateDto> selectTemplatesByOrderNo(
      @Param("orderNo") Long orderNo,
      @Param("type") String type,
      @Param("categoryNo") Long categoryNo,
      @Param("isDownloaded") Integer isDownloaded,
      @Param("keyword") String keyword
  );
  
  // 주문 상세 템플릿 개수
  int countTemplatesByOrderNo(
      @Param("orderNo") Long orderNo,
      @Param("type") String type,
      @Param("categoryNo") Long categoryNo,
      @Param("isDownloaded") Integer isDownloaded,
      @Param("keyword") String keyword
  );
  
  // 에디터 기반 템플릿 상세 조회
  ClientEditorTemplateDetailDto findEditorTemplateDetail(@Param("templateNo") Long templateNo);
  
  // 파일 기반 템플릿 상세 조회
  ClientFileTemplateDetailDto findFileTemplateDetail(@Param("templateNo") Long templateNo);
  
  // 다운로드 상태 변경
  void updateTemplateDownloaded(@Param("orderNo") Long orderNo, @Param("tmplNo") Long tmplNo);
  
  // 다운로드 상태 조회 (전체 기준)
  Boolean selectIsDownloadedByOrderNo(@Param("orderNo") Long orderNo);
  
  // 다운로드 상태 조회 (개별 기준)
  Boolean selectIsDownloadedByOrderNoAndTmplNo(
      @Param("orderNo") Long orderNo,
      @Param("tmplNo") Long tmplNo
  );
}