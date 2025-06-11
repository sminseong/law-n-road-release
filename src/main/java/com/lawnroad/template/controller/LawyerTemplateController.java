package com.lawnroad.template.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawnroad.common.util.FileStorageUtil;
import com.lawnroad.template.dto.*;
import com.lawnroad.template.service.LawyerTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

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
  
  /**
   * 템플릿 상세 조회
   * @param templateNo 템플릿 PK
   * @param type 템플릿 유형 (EDITOR 또는 FILE)
   */
  @GetMapping("/{templateNo}")
  public ResponseEntity<?> getTemplateDetail(
      @PathVariable Long templateNo,
      @RequestParam String type
  ) {
    if ("EDITOR".equalsIgnoreCase(type)) {
      EditorTemplateDetailDto dto = templateService.getEditorTemplateDetail(templateNo);
      return ResponseEntity.ok(dto);
    } else if ("FILE".equalsIgnoreCase(type)) {
      FileTemplateDetailDto dto = templateService.getFileTemplateDetail(templateNo);
      return ResponseEntity.ok(dto);
    } else {
      return ResponseEntity.badRequest().body("잘못된 템플릿 유형입니다: " + type);
    }
  }
  
  /**
   * 템플릿 수정 (복제 방식)
   * 1. 기존 썸네일·파일 정보 조회
   * 2. 썸네일 교체 시 새로 저장, 아니면 기존 유지
   * 3. 파일 삭제/추가 처리 후 최종 JSON 생성
   * 4. 서비스로 복제·수정 위임
   */
  /**
   * 템플릿 수정 (복제 방식)
   * 1. 기존 썸네일·파일 메타 조회
   * 2. 썸네일 교체 시 새로 저장, 아니면 기존 유지
   * 3. 프론트에서 전달된 pathJson(삭제된 항목 제외) 파싱 + 신규 파일 메타 추가
   * 4. 서비스로 복제·수정 위임
   */
  @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> updateTemplate(@ModelAttribute LawyerTemplateUpdateDto dto) {
    Long lawyerNo = 1L;  // TODO: Authentication 적용 후 변경
    dto.setUserNo(lawyerNo);
    String type = dto.getType();
    String thumbnailPath = null;
    List<String> uploadedPaths = new ArrayList<>();
    
    try {
      // 1) 기존 메타 조회
      FileTemplateDetailDto origin = null;
      if (dto.getNo() != null && "FILE".equalsIgnoreCase(type)) {
        origin = templateService.getFileTemplateDetail(dto.getNo());
      }
      
      // 2) 썸네일 처리
      MultipartFile thumbFile = dto.getFile();
      if (thumbFile != null && !thumbFile.isEmpty()) {
        thumbnailPath = fileStorageUtil.save(
            thumbFile,
            "uploads/lawyers/" + lawyerNo + "/thumbnails",
            null
        );
        uploadedPaths.add(thumbnailPath);
      } else if (origin != null) {
        thumbnailPath = origin.getThumbnailPath();
      }
      
      // 3) 파일 메타 처리
      List<Map<String,String>> resultList = new ArrayList<>();
      
      // 3-1) 프론트에서 삭제된 항목을 제외한 pathJson이 넘어온 경우
      if (dto.getPathJson() != null) {
        resultList = objectMapper.readValue(
            dto.getPathJson(),
            new TypeReference<List<Map<String,String>>>() {}
        );
      }
      // 3-2) 프론트에서 pathJson이 없고, origin이 있는 경우 origin 메타 사용
      else if (origin != null) {
        resultList = objectMapper.readValue(
            origin.getPathJson(),
            new TypeReference<List<Map<String,String>>>() {}
        );
      }
      
      // 3-3) 신규 업로드 파일이 있으면 저장 후 메타 추가
      if (dto.getTemplateFiles() != null) {
        for (MultipartFile f : dto.getTemplateFiles()) {
          if (!f.isEmpty()) {
            String saved = fileStorageUtil.save(
                f,
                "uploads/lawyers/" + lawyerNo + "/templates",
                null
            );
            uploadedPaths.add(saved);
            Map<String,String> meta = new HashMap<>();
            meta.put("originalName", f.getOriginalFilename());
            meta.put("savedPath", saved);
            resultList.add(meta);
          }
        }
      }
      
      // 3-4) 최종 JSON 세팅
      String finalPathJson = objectMapper.writeValueAsString(resultList);
      dto.setPathJson(finalPathJson);
      
      // 4) 서비스 호출
      templateService.updateTemplateByClone(dto, thumbnailPath);
      return ResponseEntity.ok("수정 완료");
      
    } catch (Exception e) {
      // 예외 시 업로드된 파일 삭제
      for (String path : uploadedPaths) {
        try { fileStorageUtil.delete(path); } catch (Exception ex) {}
      }
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("수정 실패: " + e.getMessage());
    }
  }
}