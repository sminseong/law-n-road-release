package com.lawnroad.advertisement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedAdvertisementResponseDto {
  private int totalCount;                              // 조회된 전체 광고 개수
  private int totalPages;                              // 선택된 limit, offset으로 계산되는 총 페이지수
  private List<AdvertisementListResponseDto> ads;      // 조회된 실제 광고 데이터
}
