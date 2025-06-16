package com.lawnroad.template.dto.order;

import lombok.Data;

@Data
public class ClientOrderListRequestDto {
  private Long userNo;
  private String status; // ORDERED, PAID, CANCELED
  private int page;
  private int limit;
}