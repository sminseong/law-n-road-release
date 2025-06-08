package com.lawnroad.template.controller;

import com.lawnroad.template.dto.TemplateCreateDto;
import com.lawnroad.template.dto.TemplateDto;
import com.lawnroad.template.dto.TemplateListDto;
import com.lawnroad.template.dto.TemplateUpdateDto;
import com.lawnroad.template.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {
  
  private final TemplateService templateService;
  
  // [공통] 전체 템플릿 목록 조회 (공개)
  @GetMapping
  public List<TemplateListDto> getAllTemplates() {
    return templateService.getAllTemplates();
  }
  
  // [사용자] 내가 구매한 템플릿 전체 조회
  @GetMapping("/user")
  public List<TemplateDto> getUserTemplates() {
    return templateService.getUserTemplatesByUserNo(1L);
  }
  
  // [사용자] 내가 구매한 템플릿 상세 조회
  @GetMapping("/user/{no}")
  public TemplateDto getUserTemplate(@PathVariable Long no) {
    return templateService.getUserTemplateByNo(no, 1L);
  }
  
  // [변호사] 내가 등록한 템플릿 전체 조회
  @GetMapping("/lawyer")
  public List<TemplateListDto> getLawyerTemplates() {
    return templateService.getLawyerTemplatesByLawyerNo(1L);
  }
  
  // [변호사] 내가 등록한 템플릿 상세 조회
  @GetMapping("/lawyer/{no}")
  public TemplateDto getLawyerTemplate(@PathVariable Long no) {
    return templateService.getLawyerTemplateByNo(no, 1L);
  }
  
  // [변호사] 템플릿 등록
  @PostMapping(value = "/lawyer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void createTemplate(
      @RequestParam("category_no") Long categoryNo,
      @RequestParam("name") String name,
      @RequestParam("price") Integer price,
      @RequestParam("discount_rate") Integer discountRate,
      @RequestParam("description") String description,
      @RequestParam("file") MultipartFile file
  ) {
    String thumbnailPath = fileStorageService.save(file); // 👉 파일 저장하고 경로 반환
    
    TemplateCreateDto dto = new TemplateCreateDto();
    dto.setCategory_no(categoryNo);
    dto.setName(name);
    dto.setPrice(price);
    dto.setDiscount_rate(discountRate);
    dto.setDescription(description);
    dto.setThumbnail_path(thumbnailPath);
    
    templateService.createTemplate(dto, 1L);
  }
  
  // [변호사] 템플릿 수정
  @PutMapping("/lawyer/{no}")
  public void updateTemplate(@PathVariable Long no, @RequestBody TemplateUpdateDto dto) {
    templateService.updateTemplate(no, dto, 1L);
  }
  
  // [변호사] 템플릿 삭제
  @DeleteMapping("/lawyer/{no}")
  public void deleteTemplate(@PathVariable Long no) {
    templateService.deleteTemplate(no, 1L);
  }
}
