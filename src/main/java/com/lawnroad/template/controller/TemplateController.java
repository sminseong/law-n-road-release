package com.lawnroad.template.controller;

import com.lawnroad.common.util.FileStorageUtil;
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
  private final FileStorageUtil fileStorageService;
  
  // [공통] 전체 템플릿 목록 조회 (공개)
  @GetMapping
  public List<TemplateListDto> getAllTemplates() {
    return templateService.getAllTemplates();
  }
  
  // [공통] 템플릿 상세 조회 (공개)
  @GetMapping("/{no}")
  public TemplateDto getTemplateByNo(@PathVariable Long no) {
    return templateService.getTemplateByNo(no);
  }
  
  // [사용자] 내가 구매한 템플릿 전체 조회
  @GetMapping("/user")
  public List<TemplateListDto> getUserTemplates() {
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
    String thumbnailPath = fileStorageService.save(file, "uploads/images"); // 👉 파일 저장하고 경로 반환
    
    TemplateCreateDto dto = new TemplateCreateDto();
    dto.setCategory_no(categoryNo);
    dto.setName(name);
    dto.setPrice(price);
    dto.setDiscount_rate(discountRate);
    dto.setDescription(description);
    if (thumbnailPath == null || thumbnailPath.isEmpty()) {
      throw new IllegalArgumentException("템플릿 파일 경로가 없습니다.");
    }
    dto.setTemplate_path("http://localhost:8080" + thumbnailPath);     // 템플릿 파일 실제 경로
    dto.setThumbnail_path("http://localhost:8080" + thumbnailPath);
    
    templateService.createTemplate(dto, 1L);
  }
  
  // [변호사] 템플릿 수정
  @PutMapping(value = "/lawyer/{no}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void updateTemplate(
      @PathVariable Long no,
      @RequestParam("category_no") Long categoryNo,
      @RequestParam("name") String name,
      @RequestParam("price") Integer price,
      @RequestParam("discount_rate") Integer discountRate,
      @RequestParam("description") String description,
      @RequestParam("thumbnail_path") String oldThumbnailPath,
      @RequestParam(value = "file", required = false) MultipartFile file
  ) {
    String thumbnailPath;
    
    if (file != null && !file.isEmpty()) {
      thumbnailPath = fileStorageService.save(file, "uploads/images");
    } else {
      thumbnailPath = oldThumbnailPath; // 기존 썸네일 유지
    }
    
    TemplateUpdateDto dto = new TemplateUpdateDto();
    dto.setCategory_no(categoryNo);
    dto.setName(name);
    dto.setPrice(price);
    dto.setDiscount_rate(discountRate);
    dto.setDescription(description);
    
    dto.setTemplate_path("http://localhost:8080" + thumbnailPath); // 템플릿 파일 실제 경로
    dto.setThumbnail_path("http://localhost:8080" + thumbnailPath); // 상대경로 or 전체경로 맞춰서
    
    templateService.updateTemplate(no, dto, 1L);
  }
  
  // [변호사] 템플릿 삭제
  @DeleteMapping("/lawyer/{no}")
  public void deleteTemplate(@PathVariable Long no) {
    templateService.deleteTemplate(no, 1L);
  }
}
