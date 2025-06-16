package com.lawnroad.template.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawnroad.common.util.NcpObjectStorageUtil;
import com.lawnroad.template.dto.*;
import com.lawnroad.template.service.LawyerTemplateService;
import com.lawnroad.template.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lawyer/templates")
public class LawyerTemplateController {
  
  private final LawyerTemplateService templateService;
  // private final FileStorageUtil fileStorageUtil;
  private final NcpObjectStorageUtil ncpObjectStorageUtil;
  private final OcrService ocrService;
  private final ObjectMapper objectMapper;
  
  /**
   * 템플릿 등록 API
   *
   * 1. 파일 기반 템플릿일 경우:
   *    - 템플릿 파일 업로드 → OCR 수행 → Gemini 검증
   *
   * 2. 에디터 기반 템플릿일 경우:
   *    - 입력된 content 그대로 → Gemini 검증
   *
   * 3. Gemini 검증 통과 시에만 썸네일 업로드 → DB 등록
   *
   * 4. 검증 실패 또는 등록 실패 시에는 모든 업로드 파일 삭제 (롤백)
   */
  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> registerTemplate(@ModelAttribute LawyerTemplateRegisterDto dto) {
    Long lawyerNo = 1L; // TODO: 로그인 연동 후 교체
    String type = dto.getType();
    String thumbnailPath = "https://kr.object.ncloudstorage.com/law-n-road/uploads/defaults/template-thumbnail.png";
    List<String> uploadedPaths = new ArrayList<>(); // 실패 시 삭제 대상
    
    try {
      String contentText = ""; // Gemini 검증용 원본 텍스트
      
      // -----------------------------------
      // [1] FILE 타입 처리
      // -----------------------------------
      // FILE 타입일 경우 OCR 수행
      if ("FILE".equalsIgnoreCase(type)) {
        List<MultipartFile> files = dto.getTemplateFiles();
        
        // 필수 파일 누락 시 종료
        if (files == null || files.isEmpty()) {
          return ResponseEntity.badRequest().body("❌ 템플릿 파일이 누락되었습니다.");
        }
        
        List<Map<String, String>> metadataList = new ArrayList<>();
        List<String> savedPaths = new ArrayList<>();
        
        // 파일 저장
        for (MultipartFile file : files) {
          if (!file.isEmpty()) {
            // PDF 페이지 수 검사
            try (PDDocument doc = PDDocument.load(file.getInputStream())) {
              if (doc.getNumberOfPages() > 10) {
                return ResponseEntity.badRequest().body(
                    "❌ PDF는 각 파일당 최대 10페이지까지만 등록할 수 있습니다: " + file.getOriginalFilename()
                );
              }
            }
            
            String savedPath = ncpObjectStorageUtil.save(
                file,
                "uploads/lawyers/" + lawyerNo + "/templates",
                null
            );
            uploadedPaths.add(savedPath);
            savedPaths.add(savedPath);  // OCR 대상 리스트
            
            Map<String, String> meta = new HashMap<>();
            meta.put("originalName", file.getOriginalFilename());
            meta.put("savedPath", savedPath);
            metadataList.add(meta);
          }
        }
        
        // pathJson 구성 후 DTO에 세팅
        String pathJson = objectMapper.writeValueAsString(metadataList);
        dto.setPathJson(pathJson);
        
        contentText = ocrService.extractTextFromUrls(savedPaths);
        // System.out.println("contentText: " + contentText);
      }
      
      // -----------------------------------
      // [2] EDITOR 타입 처리
      // -----------------------------------
      else if ("EDITOR".equalsIgnoreCase(type)) {
        contentText = dto.getContent();
      }
      
      // -----------------------------------
      // [3] Gemini AI 검증
      // -----------------------------------
//      boolean isValid = geminiService.validateTemplateContent(
//          contentText,
//          dto.getName(),
//          dto.getDescription()
//      ); // TODO: 내용 정제 필요
//
//      if (!isValid) {
//        // 검증 실패 → 등록 거부 + 파일 삭제
//        for (String path : uploadedPaths) {
//          ncpObjectStorageUtil.delete(path);
//        }
//        return ResponseEntity.badRequest().body("상품 설명이 실제 내용과 일치하지 않습니다.");
//      }
      
      // -----------------------------------
      // [4] 썸네일 저장 (검증 통과 후)
      // -----------------------------------
      if (dto.getFile() != null && !dto.getFile().isEmpty()) {
        thumbnailPath = ncpObjectStorageUtil.save(
            dto.getFile(),
            "uploads/lawyers/" + lawyerNo + "/thumbnails",
            null
        );
        uploadedPaths.add(thumbnailPath);
      }
      
      // -----------------------------------
      // [5] 최종 등록 처리
      // -----------------------------------
      templateService.registerTemplate(dto, thumbnailPath);
      return ResponseEntity.ok("등록 완료");
      
    } catch (Exception e) {
      // 예외 발생 시 모든 업로드 파일 삭제
      for (String path : uploadedPaths) {
        try {
          if (!path.equals("https://kr.object.ncloudstorage.com/law-n-road/uploads/defaults/template-thumbnail.png")) {
            ncpObjectStorageUtil.delete(path);
          }
        } catch (Exception ex) {
          System.err.println("❌ 파일 삭제 실패: " + path);
        }
      }
      return ResponseEntity.internalServerError().body("❌ 등록 실패: " + e.getMessage());
    }
  }
  
//  /**
//   * 템플릿 등록 API
//   * 1. 썸네일 저장
//   * 2. 템플릿 타입 확인해서 분기 처리
//   * 3. 등록 서비스 호출
//   */
//  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//  public ResponseEntity<String> registerTemplate(@ModelAttribute LawyerTemplateRegisterDto dto) {
//    Long lawyerNo = 1L;  // 로그인 미적용 상태 → 임시 고정
//    String type = dto.getType();
//    String thumbnailPath = "https://kr.object.ncloudstorage.com/law-n-road/uploads/defaults/template-thumbnail.png";
//
//    // 저장된 파일 경로들 (실패 시 삭제용)
//    List<String> uploadedPaths = new ArrayList<>();
//
//    try {
//      // 1. 썸네일 저장 (없으면 기본 이미지 유지)
//      if (dto.getFile() != null && !dto.getFile().isEmpty()) {
//        thumbnailPath = ncpObjectStorageUtil.save(
//            dto.getFile(),
//            "uploads/lawyers/" + lawyerNo + "/thumbnails",
//            null
//        );
//        uploadedPaths.add(thumbnailPath);
//      }
//
//      // 2. 파일 기반 템플릿 파일 저장
//      if ("FILE".equalsIgnoreCase(type)) {
//        List<MultipartFile> files = dto.getTemplateFiles();
//
//        if (files == null || files.isEmpty()) {
//          return ResponseEntity.badRequest().body("템플릿 파일이 누락되었습니다.");
//        }
//
//        List<Map<String, String>> metadataList = new ArrayList<>();
//
//        for (MultipartFile file : files) {
//          if (!file.isEmpty()) {
//            String savedPath = ncpObjectStorageUtil.save(
//                file,
//                "uploads/lawyers/" + lawyerNo + "/templates",
//                null
//            );
//            uploadedPaths.add(savedPath);
//
//            Map<String, String> meta = new HashMap<>();
//            meta.put("originalName", file.getOriginalFilename());
//            meta.put("savedPath", savedPath);
//            metadataList.add(meta);
//          }
//        }
//
//        String pathJson = objectMapper.writeValueAsString(metadataList);
//        dto.setPathJson(pathJson);
//      }
//
//      // 3. 등록 처리
//      templateService.registerTemplate(dto, thumbnailPath);
//      return ResponseEntity.ok("등록 완료");
//
//    } catch (Exception e) {
//      // 실패 시 업로드된 파일 모두 삭제
//      for (String path : uploadedPaths) {
//        try {
//          if (!"https://kr.object.ncloudstorage.com/law-n-road/uploads/defaults/template-thumbnail.png".equals(path)) {
//            ncpObjectStorageUtil.delete(path);
//          }
//        } catch (Exception ex) {
//          // 로그 정도만 출력하고 무시
//          System.err.println("파일 삭제 실패: " + path);
//        }
//      }
//      return ResponseEntity.internalServerError().body("등록 실패: " + e.getMessage());
//    }
//  }
  
  /**
   * 변호사 본인 템플릿 목록 조회 API
   *
   * @param condition 검색 조건 (페이지, 카테고리, 정렬 등)
   * @return 템플릿 목록 + 총 개수 + 총 페이지 수
   */
  @GetMapping
  public ResponseEntity<TemplateListResponseDto> getMyTemplates(TemplateSearchConditionDto condition) {
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
      LawyerEditorTemplateDetailDto dto = templateService.getEditorTemplateDetail(templateNo);
      return ResponseEntity.ok(dto);
    } else if ("FILE".equalsIgnoreCase(type)) {
      LawyerFileTemplateDetailDto dto = templateService.getFileTemplateDetail(templateNo);
      return ResponseEntity.ok(dto);
    } else {
      return ResponseEntity.badRequest().body("잘못된 템플릿 유형입니다: " + type);
    }
  }
  
  // 1) 메타데이터만 업데이트
  @PostMapping(value = "/update-meta", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> updateTemplateMeta(
      @ModelAttribute TemplateDto dto,               // 모든 메타데이터 필드
      @RequestParam(value = "file", required = false) MultipartFile thumbFile,
      @RequestParam(value = "removeThumbnail", required = false) Integer removeThumbnail
  ) {
    Long lawyerNo = 1L; // TODO: 인증 적용 후 교체
    dto.setUserNo(lawyerNo);
    
    String thumbnailPath = null;
    
    try {
      // 1. 썸네일 분기
      if (removeThumbnail != null && removeThumbnail == 1) {
        // 썸네일 제거
        thumbnailPath = "https://kr.object.ncloudstorage.com/law-n-road/uploads/defaults/template-thumbnail.png";
      } else if (thumbFile != null && !thumbFile.isEmpty()) {
        // 썸네일 교체
        thumbnailPath = ncpObjectStorageUtil.save(
            thumbFile,
            "uploads/lawyers/" + lawyerNo + "/thumbnails",
            null
        );
      } // 아니면 기존 경로를 유지(=null)
      
      // 2. 서비스 호출 (썸네일 경로 포함)
      templateService.updateTemplateMeta(dto, thumbnailPath);
      
      return ResponseEntity.ok("메타데이터 수정 완료");
    } catch (Exception e) {
      // (썸네일 업로드 실패 시 롤백 등 필요하면 추가)
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("수정 실패: " + e.getMessage());
    }
  }
  
  /**
   * 2) 본문/첨부파일까지 바뀌는 경우 (복제)
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
    String thumbnailPath = "/uploads/defaults/template-thumbnail.png";
    List<String> uploadedPaths = new ArrayList<>();
    
    try {
      // 1) 기존 메타 조회 (무조건 조회)
      LawyerEditorTemplateDetailDto editorOrigin = null;
      LawyerFileTemplateDetailDto fileOrigin = null;
      if ("EDITOR".equalsIgnoreCase(type)) {
        editorOrigin = templateService.getEditorTemplateDetail(dto.getNo());
      } else {
        fileOrigin = templateService.getFileTemplateDetail(dto.getNo());
      }
      
      // 2) 썸네일 처리
      MultipartFile thumbFile = dto.getFile();
      boolean shouldRemoveThumbnail = dto.getRemoveThumbnail() != null && dto.getRemoveThumbnail() == 1;
      
      if (shouldRemoveThumbnail) {
        // 삭제 요청이면 기본 썸네일
        thumbnailPath = "https://kr.object.ncloudstorage.com/law-n-road/uploads/defaults/template-thumbnail.png";
      } else if (thumbFile != null && !thumbFile.isEmpty()) {
        // 새 파일 업로드
        thumbnailPath = ncpObjectStorageUtil.save(
            thumbFile,
            "uploads/lawyers/" + lawyerNo + "/thumbnails",
            null
        );
        uploadedPaths.add(thumbnailPath);
      } else if ("EDITOR".equalsIgnoreCase(type) && editorOrigin != null) {
        thumbnailPath = editorOrigin.getThumbnailPath();
      } else if ("FILE".equalsIgnoreCase(type) && fileOrigin != null) {
        thumbnailPath = fileOrigin.getThumbnailPath();
      } else {
        thumbnailPath = "https://kr.object.ncloudstorage.com/law-n-road/uploads/defaults/template-thumbnail.png";
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
      else if ("FILE".equalsIgnoreCase(type) && fileOrigin != null) {
        resultList = objectMapper.readValue(
            fileOrigin.getPathJson(),
            new TypeReference<List<Map<String,String>>>() {}
        );
      }
      
      // 3-3) 신규 업로드 파일이 있으면 저장 후 메타 추가
      if (dto.getTemplateFiles() != null) {
        for (MultipartFile f : dto.getTemplateFiles()) {
          if (!f.isEmpty()) {
            String saved = ncpObjectStorageUtil.save(
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
        try { ncpObjectStorageUtil.delete(path); } catch (Exception ex) {}
      }
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("수정 실패: " + e.getMessage());
    }
  }
}