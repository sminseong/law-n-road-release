package com.lawnroad.template.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class LawyerTemplateRegisterDto {
  
  // 공통 필드
  // 작성자 (템플릿을 등록하는 변호사의 유저no)
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
  // 썸네일 이미지 경로
  private MultipartFile file;
  
  // 에디터 기반
  // 템플릿 본문
  private String content;
  // 템플릿에 사용된 변수 JSON
  private String varJson;
  // AI 활용 동의
  private Integer aiEnabled;
  
  // 파일 기반
  // 템플릿 파일 경로
  // MultipartFile 리스트로 다중 바인딩
  private List<MultipartFile> templateFiles;
  // 실제 DB에 저장할 JSON 메타 (원본명 + 저장경로 리스트)
  private String pathJson;
}