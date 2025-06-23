package com.lawnroad.admin.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdPurchaseListResponseDto {
  /** 조회된 전체 광고 구매 내역 개수 */
  private int totalCount;
  /** 계산된 전체 페이지 수 */
  private int totalPages;
  /** 현재 페이지에 해당하는 광고 구매 내역 리스트 */
  private List<AdPurchaseDto> items;
}
