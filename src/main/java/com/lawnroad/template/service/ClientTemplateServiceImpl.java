package com.lawnroad.template.service;

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
}
