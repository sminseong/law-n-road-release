package com.lawnroad.template.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawnroad.account.service.LawyerPointService;
import com.lawnroad.ai.dto.ValidationResultDto;
import com.lawnroad.ai.service.AiService;
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
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lawyer/templates")
public class LawyerTemplateController {
  
  private final LawyerTemplateService templateService;
  // private final FileStorageUtil fileStorageUtil;
  private final NcpObjectStorageUtil ncpObjectStorageUtil;
  private final OcrService ocrService;
  private final ObjectMapper objectMapper;
  private final AiService aiService;
  private final LawyerPointService lawyerPointService;
  
  /**
   * 템플릿 등록 API (변호사 권한)
   * 1. 입력 타입 분기: FILE 또는 EDITOR
   *    * FILE인 경우:
   *        - PDF 저장
   *        - OCR 수행
   *        - pathJson 저장
   *    * EDITOR인 경우:
   *        - 입력된 content 그대로 사용
   * 2. Gemini 검증:
   *    * 실패 시 이유 출력 + 응답 반환
   * 3. 썸네일 업로드 (검증 통과 시)
   * 4. 최종 등록
   */
  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> registerTemplate(@ModelAttribute LawyerTemplateRegisterDto dto) {
    Long lawyerNo = 1L; // TODO: 로그인 연동 후 교체
    String type = dto.getType();
    // 기본 값
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
        
        if (contentText == null || contentText.trim().isEmpty()) {
          return ResponseEntity.badRequest().body("❌ OCR 분석에 실패하여 문서를 수정할 수 없습니다.");
        }
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
      ValidationResultDto result = aiService.validateTemplateContent(
          contentText,
          dto.getName(),
          dto.getDescription()
      );
      
      boolean isValid = result.isPassed();
      
      if (!isValid) {
        String reasonText = String.join("\n", result.getReasons());
        System.out.println("❌ 다음 사유로 문서 검증에 실패했습니다.\n" + reasonText);
        String responseText = "❌ 다음 사유로 문서 검증에 실패했습니다.\n\n" + reasonText;
        
        return ResponseEntity
            .badRequest()
            .body(responseText);  // 문자열로 실패 사유 전송
      }
      
      // -----------------------------------
      // [4] 썸네일 저장 (검증 통과 후) **
      // -----------------------------------
      if (dto.getFile() != null && !dto.getFile().isEmpty()) {
        thumbnailPath = ncpObjectStorageUtil.save(
            dto.getFile(),
            "uploads/lawyers/" + lawyerNo + "/thumbnails",  //profile
            null
        );
        uploadedPaths.add(thumbnailPath);
      }
      
      // -----------------------------------
      // [5] 최종 등록 처리
      // -----------------------------------
      templateService.registerTemplate(dto, thumbnailPath);
      lawyerPointService.addPoint(lawyerNo, 100);
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
  
  /**
   * 변호사 본인 템플릿 목록 조회 API (변호사 권한)
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
   * 변호사 템플릿 삭제 API (변호사 권한)
   *
   * @param templateNo 템플릿 번호
   */
  @DeleteMapping("/{templateNo}")
  public ResponseEntity<String> deleteTemplate(@PathVariable Long templateNo) {
    templateService.deleteTemplate(templateNo);
    return ResponseEntity.ok("삭제 완료");
  }
  
  /**
   * 템플릿 상세 조회 (변호사 권한)
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
  
  // 1) 메타데이터만 업데이트 (변호사 권한)
  @PostMapping(value = "/update-meta", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> updateTemplateMeta(
      @ModelAttribute TemplateDto dto,               // 모든 메타데이터 필드
      @RequestParam(value = "file", required = false) MultipartFile thumbFile,
      @RequestParam(value = "removeThumbnail", required = false) Integer removeThumbnail
  ) {
    System.out.println("✅ update-meta 도착!");
    System.out.println("dto: " + dto);
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
   * 1. 기존 템플릿 조회 (변호사 권한)
   * 2. FILE인 경우 pathJson 병합 + 신규 파일 저장
   * 3. 본문 추출
   *    * FILE → OCR
   *    * EDITOR → content 직접 사용
   * 4. Gemini 검증 수행
   * 5. 검증 통과 시 썸네일 처리
   * 6. 최종 DB 업데이트 (복제/수정 위임)
   * 7. 중간 오류 시 롤백 (업로드된 파일 삭제)
   */
  @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> updateTemplate(
      @ModelAttribute LawyerTemplateUpdateDto dto
  ) {
    Long lawyerNo = 1L; // TODO: 인증 연동 후 교체
    dto.setUserNo(lawyerNo);
    String type = dto.getType();
    String thumbnailPath = "https://kr.object.ncloudstorage.com/law-n-road/uploads/defaults/template-thumbnail.png";
    List<String> uploadedPaths = new ArrayList<>();
    String finalThumbPath = null;
    
    try {
      // 1. 기존 템플릿 조회
      LawyerEditorTemplateDetailDto editorOrigin = null;
      LawyerFileTemplateDetailDto fileOrigin = null;
      if ("EDITOR".equalsIgnoreCase(type)) {
        editorOrigin = templateService.getEditorTemplateDetail(dto.getNo());
      } else {
        fileOrigin = templateService.getFileTemplateDetail(dto.getNo());
      }
      
      // 2. pathJson 처리 + 신규 파일 메타 추가
      List<Map<String, String>> resultList = new ArrayList<>();
      if (dto.getPathJson() != null) {
        resultList = objectMapper.readValue(dto.getPathJson(), new TypeReference<>() {});
      }
      
      if (dto.getTemplateFiles() != null) {
        for (MultipartFile f : dto.getTemplateFiles()) {
          if (!f.isEmpty()) {
            String saved = ncpObjectStorageUtil.save(
                f,
                "uploads/lawyers/" + lawyerNo + "/templates",
                null
            );
            uploadedPaths.add(saved);
            Map<String, String> meta = new HashMap<>();
            meta.put("originalName", f.getOriginalFilename());
            meta.put("savedPath", saved);
            resultList.add(meta);
          }
        }
      }
      String finalPathJson = objectMapper.writeValueAsString(resultList);
      dto.setPathJson(finalPathJson);
      
      // 3. OCR 수행 또는 content 직접 사용
      String contentText = "";
      if ("FILE".equalsIgnoreCase(type)) {
        List<String> filePaths = resultList.stream()
            .map(m -> m.get("savedPath"))
            .collect(Collectors.toList());
        contentText = ocrService.extractTextFromUrls(filePaths);
        
        if (contentText == null || contentText.trim().isEmpty()) {
          return ResponseEntity.badRequest().body("❌ OCR 분석에 실패하여 문서를 수정할 수 없습니다.");
        }
      } else if ("EDITOR".equalsIgnoreCase(type)) {
        contentText = dto.getContent();
      }
      
      // 4. Gemini 검증
      ValidationResultDto result = aiService.validateTemplateContent(
          contentText,
          dto.getName(),
          dto.getDescription()
      );
      
      if (!result.isPassed()) {
        String reasonText = String.join("\n", result.getReasons());
        return ResponseEntity.badRequest().body("❌ 다음 사유로 문서 검증에 실패했습니다.\n\n" + reasonText);
      }
      
      // 5. 썸네일 처리
      if (dto.getRemoveThumbnail() != null && dto.getRemoveThumbnail() == 1) {
        finalThumbPath = "https://kr.object.ncloudstorage.com/law-n-road/uploads/defaults/template-thumbnail.png";
      } else if (dto.getFile() != null && !dto.getFile().isEmpty()) {
        finalThumbPath = ncpObjectStorageUtil.save(
            dto.getFile(),
            "uploads/lawyers/" + lawyerNo + "/thumbnails",
            null
        );
        uploadedPaths.add(finalThumbPath);
      } else {
        if ("EDITOR".equalsIgnoreCase(type) && editorOrigin != null) {
          finalThumbPath = editorOrigin.getThumbnailPath();
        } else if ("FILE".equalsIgnoreCase(type) && fileOrigin != null) {
          finalThumbPath = fileOrigin.getThumbnailPath();
        } else {
          finalThumbPath = "https://kr.object.ncloudstorage.com/law-n-road/uploads/defaults/template-thumbnail.png";
        }
      }
      
      // 6. 서비스 호출 (복제 수정 위임)
      templateService.updateTemplateByClone(dto, finalThumbPath);
      return ResponseEntity.ok("수정 완료");
      
    } catch (Exception e) {
      for (String path : uploadedPaths) {
        try { ncpObjectStorageUtil.delete(path); } catch (Exception ignored) {}
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("❌ 수정 실패: " + e.getMessage());
    }
  }
  
}