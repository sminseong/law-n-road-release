package com.lawnroad.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdPurchaseSearchConditionDto {
  // 현재 페이지 번호 (1부터 시작)
  private int page = 1;
  // 페이지당 항목 수
  private int limit = 10;
  
  // 조회 조건
  private String adType;           // MAIN, SUB
  private Integer adStatus;        // 0(비활성)/1(활성)
  private String approvalStatus;   // PENDING, APPROVED, REJECTED
  private String keyword;          // 검색어
  
  // MyBatis 오프셋 계산
  public int getOffset() {
    return (page - 1) * limit;
  }
}