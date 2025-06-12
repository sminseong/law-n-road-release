package com.lawnroad.template.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// LawyerTemplateUpdateDTO와 유사하지만 no 정보를 위해 추가된 DTO
@Data
public class LawyerTemplateUpdateDto {
  
  // 수정 대상 템플릿 PK
  private Long no;
  
  // 공통 필드
  // 템플릿명
  private String name;
  // 가격
  private Integer price;
  // 할인율
  private Integer discountRate;
  // 카테고리
  private Long categoryNo;
  // 설명
  private String description;
  // 저장 형식
  private String type; // FILE or EDITOR
  // 썸네일 이미지 파일 (선택)
  private MultipartFile file;
  
  // 에디터 기반
  // 템플릿 본문
  private String content;
  // 템플릿에 사용된 변수 JSON
  private String varJson;
  // AI 활용 동의
  private Integer aiEnabled;
  
  // 파일 기반
  // 템플릿 파일 (업로드 파일 리스트)
  private List<MultipartFile> templateFiles;
  // 실제 DB에 저장할 JSON 메타 (원본명 + 저장경로 리스트)
  private String pathJson;
  
  // 로그인한 유저번호 (Controller에서 주입)
  private Long userNo;
  // 1이면 삭제, 0 또는 null이면 유지
  private Integer removeThumbnail;
}