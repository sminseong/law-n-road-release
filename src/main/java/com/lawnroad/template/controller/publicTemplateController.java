package com.lawnroad.template.controller;

import com.lawnroad.template.dto.ClientTemplateDetailResponseDto;
import com.lawnroad.template.dto.TemplateListResponseDto;
import com.lawnroad.template.dto.TemplateSearchConditionDto;
import com.lawnroad.template.service.ClientTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/templates")
public class publicTemplateController {
  
  private final ClientTemplateService clientTemplateService;
  
  /**
   * 전체 템플릿 목록 조회 (공개권한)
   * GET /api/client/templates?page=1&limit=20&categoryNo=3&keyword=계약서&type=EDITOR&sort=popular
   */
  @GetMapping
  public TemplateListResponseDto getAllTemplates(TemplateSearchConditionDto condition) {
    System.out.println("condition=" + condition);
    return clientTemplateService.findAllTemplates(condition);
  }
  
  /**
   * 템플릿 상세 정보 조회 (공개권한)
   * @param no 템플릿 번호
   * @return 템플릿 + 등록한 변호사 정보
   */
  @GetMapping("/{no}")
  public ResponseEntity<ClientTemplateDetailResponseDto> getTemplateByNo(@PathVariable Long no) {
    System.out.println( "no=" + no);
    ClientTemplateDetailResponseDto dto = clientTemplateService.findTemplateDetailByNo(no);
    return ResponseEntity.ok(dto);
  }
}
