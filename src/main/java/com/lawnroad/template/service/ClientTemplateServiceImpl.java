package com.lawnroad.template.service;

import com.lawnroad.template.dto.ClientTemplateDetailResponseDto;
import com.lawnroad.template.dto.TemplateDto;
import com.lawnroad.template.dto.TemplateListResponseDto;
import com.lawnroad.template.dto.TemplateSearchConditionDto;
import com.lawnroad.template.mapper.ClientTemplateMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
