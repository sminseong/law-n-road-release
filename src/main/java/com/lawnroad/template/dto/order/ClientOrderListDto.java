package com.lawnroad.template.dto.order;

import lombok.Data;

@Data
public class ClientOrderListDto {
  private Long orderNo;
  private String status;         // ORDERED, PAID, CANCELED
  private String orderDate;      // 주문일자 (yyyy-MM-dd)
  private Long totalAmount;      // 총 결제 금액
  
  private String firstTemplateName;  // 대표 템플릿명
  private int templateCount;         // 전체 상품 수
  private int isDownloaded;
}