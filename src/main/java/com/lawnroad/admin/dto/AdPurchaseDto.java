package com.lawnroad.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdPurchaseDto {
  private Long no;
  private Long ordersNo;
  private Long userNo;
  private String advertiserName;
  private String adPath;
  private String adType;
  private String mainText;
  private String detailText;
  private String tipText;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Boolean adStatus;
  private String approvalStatus;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
