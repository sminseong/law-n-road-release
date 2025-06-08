package com.lawnroad.template.service;

import com.lawnroad.template.dto.*;

import java.util.List;

public interface TemplateService {
  // 공통
  List<TemplateListDto> getAllTemplates();
  
  // 사용자
  List<TemplateDto> getUserTemplatesByUserNo(Long userNo);
  TemplateDto getUserTemplateByNo(Long no, Long userNo);
  
  // 변호사
  List<TemplateListDto> getLawyerTemplatesByLawyerNo(Long lawyerNo);
  TemplateDto getLawyerTemplateByNo(Long no, Long lawyerNo);
  void createTemplate(TemplateCreateDto dto, Long lawyerNo);
  void updateTemplate(Long no, TemplateUpdateDto dto, Long lawyerNo);
  void deleteTemplate(Long no, Long lawyerNo);
}