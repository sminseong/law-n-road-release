package com.lawnroad.template.dto;

import lombok.Data;

import java.time.LocalDateTime;

// 템플릿 테이블 엔티티

@Data
public class TemplateDto {
  // 템플릿 no
  private Long no;
  // 유저 no (작성한 변호사)
  private Long userNo;
  // 카테고리 no
  private Long categoryNo;
  // 템플릿 타입
  private String type; // "FILE" or "EDITOR"
  // 템플릿명
  private String name;
  // 상세설명
  private String description;
  // 가격
  private Integer price;
  // 썸네일 이미지 경로
  private String thumbnailPath;
  // 누적 판매량
  private Integer salesCount;
  // 할인률
  private Integer discountRate;
  // 삭제 여부
  private Boolean isDeleted;
  // 생성일시
  private LocalDateTime createdAt;
  // 수정일시
  private LocalDateTime updatedAt;
}