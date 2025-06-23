package com.lawnroad.advertisement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdvertisementListResponseDto {
  private Long adNo;
  private String adType;
  private String mainText;
  private String detailText;
  private String tipText;
  private String adPath;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private String approvalStatus;
  private Integer adStatus;
}