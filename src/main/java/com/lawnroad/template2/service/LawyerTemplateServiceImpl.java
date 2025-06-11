package com.lawnroad.template2.service;

import com.lawnroad.template2.dto.LawyerTemplateRegisterDto;
import com.lawnroad.template2.dto.TemplateDto;
import com.lawnroad.template2.mapper.LawyerTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LawyerTemplateServiceImpl implements LawyerTemplateService {
  
  private final LawyerTemplateMapper templateMapper;
  
  @Override
  public void registerTemplate(LawyerTemplateRegisterDto dto, String thumbnailPath, String filePath) {
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
    if ("FILE".equals(dto.getType())) {
      templateMapper.insertFileBasedTemplate(template.getNo(), filePath);
    }
  }
}