package com.lawnroad.template.controller;

import com.lawnroad.template.dto.TemplateListResponseDto;
import com.lawnroad.template.dto.TemplateSearchConditionDto;
import com.lawnroad.template.service.ClientTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client/templates")
public class ClientTemplateController {
  
  private final ClientTemplateService clientTemplateService;
  
  /**
   * 전체 템플릿 목록 조회 (사용자용)
   * GET /api/client/templates?page=1&limit=20&categoryNo=3&keyword=계약서&type=EDITOR&sort=popular
   */
  @GetMapping
  public TemplateListResponseDto getAllTemplates(TemplateSearchConditionDto condition) {
    return clientTemplateService.findAllTemplates(condition);
  }
}