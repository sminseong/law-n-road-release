package com.lawnroad.template.mapper;

import com.lawnroad.template.dto.ClientTemplateDetailResponseDto;
import com.lawnroad.template.dto.TemplateDto;
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
  List<ClientOrderListDto> selectOrdersByUserNo(@Param("userNo") Long userNo,
                                                @Param("status") String status,
                                                @Param("limit") int limit,
                                                @Param("offset") int offset);
  
  // 주문 개수 조회
  int countOrdersByUserNo(@Param("userNo") Long userNo,
                          @Param("status") String status);
  
  // 주문 상세 템플릿 목록
  List<ClientOrderTemplateDto> selectTemplatesByOrderNo(@Param("orderNo") Long orderNo,
                                                        @Param("type") String type,
                                                        @Param("categoryNo") Long categoryNo,
                                                        @Param("isDownloaded") Integer isDownloaded);
  
  // 주문 상세 템플릿 개수
  int countTemplatesByOrderNo(@Param("orderNo") Long orderNo,
                              @Param("type") String type,
                              @Param("categoryNo") Long categoryNo,
                              @Param("isDownloaded") Integer isDownloaded);
}