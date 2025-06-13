package com.lawnroad.template.dto;

import lombok.Data;

// 사용자(클라이언트)가 상세조회 시 받는 템플릿 상세 응답 DTO
@Data
public class ClientTemplateDetailResponseDto {
  
  // --- 템플릿 정보 ---
  
  // 템플릿 고유 번호
  private Long no;
  
  // 템플릿명
  private String name;
  
  // 카테고리명
  private String categoryName;
  
  // 가격 (정가)
  private Integer price;
  
  // 할인율
  private Integer discountRate;
  
  // 썸네일 이미지 경로
  private String thumbnailPath;
  
  // 템플릿 타입 (EDITOR 또는 FILE)
  private String type;
  
  // 누적 판매량
  private Integer salesCount;
  
  // 템플릿 설명
  private String description;
  
  // --- 변호사 정보 ---
  
  // 변호사 이름
  private String lawyerName;
  
  // 프로필 이미지 경로
  private String profile;
  
  // 변호사 한줄 소개 (아직 DB에는 없음)
  private String shortIntro;
  
  // 변호사 상세 소개 (아직 DB에는 없음)
  private String longIntro;
  
  // 도로명 주소
  private String roadAddress;
  
  // 지번 주소
  private String landAddress;
  
  // 상세 주소
  private String detailAddress;
  
  // 사무실 전화번호
  private String officeNumber;
}