package com.lawnroad.template.dto;

import lombok.Data;

// 클라이언트용 템플릿 상세 응답 DTO
@Data
public class ClientTemplateDetailResponseDto {
  
  // --- 템플릿 정보 ---
  
  private Long no;                     // 템플릿 번호
  private String name;                 // 템플릿명
  private Integer price;              // 정가
  private Integer discountRate;       // 할인율
  private String categoryName;        // 카테고리명
  private String type;                // 템플릿 타입 (EDITOR / FILE)
  private String thumbnailPath;       // 썸네일 경로
  private Integer salesCount;         // 누적 판매량
  private String description;         // 설명
  
  // --- 변호사 정보 ---
  
  private String lawyerNo;          // 변호사 번호
  private String lawyerName;          // 변호사 이름
  private String profile;             // 프로필 이미지 경로
  private String fullAddress;         // 도로명 주소
  private String officeNumber;        // 사무실 전화번호
  private String lawyerOfficeName;    // 사무실 이름
  private String shortIntro;
  private String longIntro;
}