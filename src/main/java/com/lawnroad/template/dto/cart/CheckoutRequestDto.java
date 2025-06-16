package com.lawnroad.template.dto.cart;

import lombok.Data;

@Data
public class CheckoutRequestDto {
  private Long userNo;
  // (추가로 필요하다면 결제수단 식별자 등 넣을 수 있음)
}