package com.lawnroad.advertisement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdvertisementDetailResponseDto {
  private Long adNo;
  private Long userNo;
  private Long ordersNo;
  private String adType;
  private String mainText;
  private String detailText;
  private String tipText;
  private String adPath;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Integer adStatus;
  private String approvalStatus;
}