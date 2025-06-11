package com.lawnroad.template.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawnroad.common.util.FileStorageUtil;
import com.lawnroad.template.dto.LawyerTemplateRegisterDto;
import com.lawnroad.template.dto.TemplateDto;
import com.lawnroad.template.dto.TemplateListResponse;
import com.lawnroad.template.dto.TemplateSearchCondition;
import com.lawnroad.template.mapper.LawyerTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LawyerTemplateServiceImpl implements LawyerTemplateService {
  
  private final LawyerTemplateMapper templateMapper;
  private final FileStorageUtil fileStorageUtil;
  private final ObjectMapper objectMapper;
  
  // 템플릿 등록
  @Override
  @Transactional
  public void registerTemplate(LawyerTemplateRegisterDto dto, String thumbnailPath) {
    // 1. 공통 템플릿 정보 insert
    TemplateDto template = new TemplateDto();
    template.setUserNo(1L);  // 로그인 전 임시 하드코딩
    template.setCategoryNo(dto.getCategoryNo());
    template.setName(dto.getName());
    template.setDescription(dto.getDescription());
    template.setPrice(dto.getPrice());
    template.setDiscountRate(dto.getDiscountRate());
    template.setThumbnailPath(thumbnailPath);
    template.setType(dto.getType());
    
    // insert 후 template.no 자동 주입됨
    templateMapper.insertTemplate(template);
    
    // 2. 에디터 기반이면 tmpl_editor_based insert
    if ("EDITOR".equals(dto.getType())) {
      templateMapper.insertEditorBasedTemplate(
          template.getNo(),
          dto.getContent(),
          dto.getVarJson(),
          dto.getAiEnabled()
      );
    }
    
    // 3. 파일 기반이면 tmpl_file_based insert
    if ("FILE".equalsIgnoreCase(dto.getType())) {
      templateMapper.insertFileBasedTemplate(template.getNo(), dto.getPathJson());
    }
  }
  
  // 템플릿 조회
  @Override
  @Transactional(readOnly = true)
  public TemplateListResponse findTemplatesByLawyerNo(Long lawyerNo, TemplateSearchCondition condition) {
    
    // 1. offset 계산
    int offset = (condition.getPage() - 1) * condition.getLimit();
    
    // 2. 템플릿 목록 조회
    List<TemplateDto> templates = templateMapper.selectMyTemplates(
        lawyerNo,
        offset,
        condition.getLimit(),
        condition.getCategoryNo(),
        condition.getKeyword(),
        condition.getType(),
        condition.getSort()
    );
    
    // 3. 총 개수 조회
    int totalCount = templateMapper.countMyTemplates(
        lawyerNo,
        condition.getCategoryNo(),
        condition.getKeyword(),
        condition.getType()
    );
    
    // 4. 총 페이지 수 계산
    int totalPages = (int) Math.ceil((double) totalCount / condition.getLimit());
    
    // 5. 응답 DTO 구성
    TemplateListResponse response = new TemplateListResponse();
    response.setTemplates(templates);
    response.setTotalCount(totalCount);
    response.setTotalPages(totalPages);
    
    return response;
  }
  
  // 템플릿 삭제
  @Override
  @Transactional
  public void deleteTemplate(Long templateNo) {
    int updated = templateMapper.markTemplateAsDeleted(templateNo);
    
    if (updated == 0) {
      throw new IllegalArgumentException("존재하지 않거나 이미 삭제된 템플릿입니다.");
    }
  }
  
}