package com.lawnroad.template.dto;

import lombok.Data;

@Data
public class TmplOrdersHistoryCreateDto {
  private Long tmplNo;    // template.no
  private Long orderNo;   // orders.no
  private Integer price;  // 결제 당시 실제 지불가 (할인가)
}