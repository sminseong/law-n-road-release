package com.lawnroad.template2.controller;


import com.lawnroad.common.util.FileStorageUtil;
import com.lawnroad.template2.dto.LawyerTemplateRegisterDto;
import com.lawnroad.template2.service.LawyerTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lawyer/templates")
public class LawyerTemplateController {
  
  private final LawyerTemplateService templateService;
  private final FileStorageUtil fileStorageUtil;
  
  /**
   * 템플릿 등록 API
   * 1. 썸네일 저장
   * 2. 템플릿 타입 확인해서 분기 처리
   * 3. 등록 서비스 호출
   */
  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> registerTemplate(@ModelAttribute LawyerTemplateRegisterDto dto) {
    try {
      // 현재 로그인 미적용 상태 → 임시 하드코딩
      Long lawyerNo = 1L;
      
      // 1. 썸네일 저장
      String thumbnailPath = fileStorageUtil.save(
          dto.getFile(),
          "uploads/lawyers/" + lawyerNo + "/thumbnails",
          null
      );
      
      String filePath = null;
      String type = dto.getType();
      
      // 2-1. 파일 기반이라면 템플릿 파일 저장
      // 2-2. 에디터 기반이면 pass (실제 저장은 서비스에서 수행)
      if ("FILE".equalsIgnoreCase(type)) {
        MultipartFile templateFile = dto.getTemplateFile();
        
        if (templateFile == null || templateFile.isEmpty()) {
          return ResponseEntity.badRequest().body("템플릿 파일이 누락되었습니다.");
        }
        
        filePath = fileStorageUtil.save(
            templateFile,
            "uploads/lawyers/" + lawyerNo + "/templates",
            null
        );
      }
      
      // 3. 등록 처리
      templateService.registerTemplate(dto, thumbnailPath, filePath);
      return ResponseEntity.ok("등록 완료");
      
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("등록 실패: " + e.getMessage());
    }
  }
}