package com.lawnroad.template.service;

import com.lawnroad.template.dto.*;
import com.lawnroad.template.mapper.TemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
  
  private final TemplateMapper templateMapper;
  
  // 공통
  @Override
  public List<TemplateListDto> getAllTemplates() {
    return templateMapper.findAll();
  }
  
  // 사용자
  @Override
  public List<TemplateListDto> getUserTemplatesByUserNo(Long userNo) {
    return templateMapper.findByUserNo(userNo);
  }
  
  @Override
  public TemplateDto getUserTemplateByNo(Long no, Long userNo) {
    return templateMapper.findByNoAndUserNo(no, userNo);
  }
  
  // 변호사
  @Override
  public List<TemplateListDto> getLawyerTemplatesByLawyerNo(Long lawyerNo) {
    return templateMapper.findByLawyerNo(lawyerNo);
  }
  
  @Override
  public TemplateDto getLawyerTemplateByNo(Long no, Long lawyerNo) {
    return templateMapper.findByNoAndLawyerNo(no, lawyerNo);
  }
  
  @Override
  public void createTemplate(TemplateCreateDto dto, Long lawyerNo) {
    dto.setUser_no(lawyerNo);
    templateMapper.insert(dto);
  }
  
  @Override
  public void updateTemplate(Long no, TemplateUpdateDto dto, Long lawyerNo) {
    templateMapper.update(no, lawyerNo, dto);
  }
  
  @Override
  public void deleteTemplate(Long no, Long lawyerNo) {
    templateMapper.delete(no, lawyerNo);
  }
}