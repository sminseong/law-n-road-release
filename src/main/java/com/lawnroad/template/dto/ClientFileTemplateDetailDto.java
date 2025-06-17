package com.lawnroad.template.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientFileTemplateDetailDto {
  
  // 템플릿 고유번호 (PK)
  private Long no;
  
  // 템플릿명
  private String name;
  
  // 가격 (원 단위)
  private Integer price;
  
  // 할인율 (% 단위)
  private Integer discountRate;
  
  // 카테고리 번호 (FK → category)
  private Long categoryNo;
  
  // 템플릿 상세 설명
  private String description;
  
  // 썸네일 이미지 경로
  private String thumbnailPath;
  
  // 템플릿 파일 경로 + 원본명 목록 (JSON 형태 문자열)
  private String pathJson;
  
  // 생성 일시
  private LocalDateTime createdAt;
  
  // 변호사 정보
  private Long lawyerNo;              // l.no → lawyer_no
  private String lawyerName;          // l.name → lawyer_name
  private String lawyerProfileImg;    // l.profile → lawyer_profile_img
  private String lawyerShortIntro;    // l.lawyer_intro → lawyer_short_intro
  private String lawyerOfficeName;    // l.office_name → lawyer_office_name
  private String fullAddress;         // CONCAT_WS → full_address
  private String lawyerOfficeTel;     // l.office_number → lawyer_office_tel
}