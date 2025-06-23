package com.lawnroad.mainsearch.dto;

import lombok.Data;

@Data
public class LawyerAdBannerDto {
  private String title;       // e.g. "교통사고 전문"
  private String desc;        // e.g. "신속한 대응과 확실한 전략으로..."
  private String image;       // e.g. "/img/ads/slider-1-1.png"
  private String link;        // e.g. "/lawyer.html"
  private Long lawyerNo;
  private String lawyerName;  // e.g. 김수영
  private String badge;       // 선택사항: tip_text → badge에 매핑
}