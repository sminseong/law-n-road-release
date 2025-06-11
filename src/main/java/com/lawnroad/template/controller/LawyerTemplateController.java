package com.lawnroad.template.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawnroad.common.util.FileStorageUtil;
import com.lawnroad.template.dto.LawyerTemplateRegisterDto;
import com.lawnroad.template.dto.TemplateListResponse;
import com.lawnroad.template.dto.TemplateSearchCondition;
import com.lawnroad.template.service.LawyerTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lawyer/templates")
public class LawyerTemplateController {
  
  private final LawyerTemplateService templateService;
  private final FileStorageUtil fileStorageUtil;
  private final ObjectMapper objectMapper;
  
  /**
   * 템플릿 등록 API
   * 1. 썸네일 저장
   * 2. 템플릿 타입 확인해서 분기 처리
   * 3. 등록 서비스 호출
   */
  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> registerTemplate(@ModelAttribute LawyerTemplateRegisterDto dto) {
    Long lawyerNo = 1L;  // 로그인 미적용 상태 → 임시 고정
    String type = dto.getType();
    String thumbnailPath = "uploads/defaults/template-thumbnail.png";
    
    // 저장된 파일 경로들 (실패 시 삭제용)
    List<String> uploadedPaths = new ArrayList<>();
    
    try {
      // 1. 썸네일 저장 (없으면 기본 이미지 유지)
      if (dto.getFile() != null && !dto.getFile().isEmpty()) {
        thumbnailPath = fileStorageUtil.save(
            dto.getFile(),
            "uploads/lawyers/" + lawyerNo + "/thumbnails",
            null
        );
        uploadedPaths.add(thumbnailPath);
      }
      
      // 2. 파일 기반 템플릿 파일 저장
      if ("FILE".equalsIgnoreCase(type)) {
        List<MultipartFile> files = dto.getTemplateFiles();
        
        if (files == null || files.isEmpty()) {
          return ResponseEntity.badRequest().body("템플릿 파일이 누락되었습니다.");
        }
        
        List<Map<String, String>> metadataList = new ArrayList<>();
        
        for (MultipartFile file : files) {
          if (!file.isEmpty()) {
            String savedPath = fileStorageUtil.save(
                file,
                "uploads/lawyers/" + lawyerNo + "/templates",
                null
            );
            uploadedPaths.add(savedPath);
            
            Map<String, String> meta = new HashMap<>();
            meta.put("originalName", file.getOriginalFilename());
            meta.put("savedPath", savedPath);
            metadataList.add(meta);
          }
        }
        
        String pathJson = objectMapper.writeValueAsString(metadataList);
        dto.setPathJson(pathJson);
      }
      
      // 3. 등록 처리
      templateService.registerTemplate(dto, thumbnailPath);
      return ResponseEntity.ok("등록 완료");
      
    } catch (Exception e) {
      // 실패 시 업로드된 파일 모두 삭제
      for (String path : uploadedPaths) {
        try {
          if (!"uploads/defaults/template-thumbnail.png".equals(path)) {
            fileStorageUtil.delete(path);
          }
        } catch (Exception ex) {
          // 로그 정도만 출력하고 무시
          System.err.println("파일 삭제 실패: " + path);
        }
      }
      return ResponseEntity.internalServerError().body("등록 실패: " + e.getMessage());
    }
  }
  
  /**
   * 변호사 본인 템플릿 목록 조회 API
   *
   * @param condition 검색 조건 (페이지, 카테고리, 정렬 등)
   * @return 템플릿 목록 + 총 개수 + 총 페이지 수
   */
  @GetMapping
  public ResponseEntity<TemplateListResponse> getMyTemplates(TemplateSearchCondition condition) {
    System.out.println("▶ condition = {}" + condition);
    Long lawyerNo = 1L;  // 로그인 미적용 상태 → 임시 고정
    return ResponseEntity.ok(templateService.findTemplatesByLawyerNo(lawyerNo, condition));
  }
  
  /**
   * 변호사 템플릿 삭제 API
   *
   * @param templateNo 템플릿 번호
   */
  @DeleteMapping("/{templateNo}")
  public ResponseEntity<String> deleteTemplate(@PathVariable Long templateNo) {
    templateService.deleteTemplate(templateNo);
    return ResponseEntity.ok("삭제 완료");
  }
}