package com.lawnroad.advertisement.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AdRegisterDto {
  private Long userNo; // 로그인 사용자 번호 (임시 하드코딩 중)
  private Long ordersNo;
  private String adType;       // "MAIN" or "SUB"
  private String mainText;
  private String detailText;
  private String tipText;
  private String startDate;    // yyyy-MM-dd
  private String endDate;      // yyyy-MM-dd
  private MultipartFile file;  // 광고 이미지
  private String adPath;  // DB insert용 실제 경로
  private int adStatus;
  private String approvalStatus;
}