package com.lawnroad.template.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawnroad.common.util.FileStorageUtil;
import com.lawnroad.template.dto.LawyerTemplateRegisterDto;
import com.lawnroad.template.dto.TemplateDto;
import com.lawnroad.template.mapper.LawyerTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LawyerTemplateServiceImpl implements LawyerTemplateService {
  
  private final LawyerTemplateMapper templateMapper;
  private final FileStorageUtil fileStorageUtil;
  private final ObjectMapper objectMapper;
  
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
}