package com.lawnroad.template.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponseDto {
  private Long orderNo;
  private Long tmplNo;
  // (필요하다면 결제 트랜잭션 ID, 결제금액, 타임스탬프 등 추가)
  
  public CheckoutResponseDto(Long orderNo) {
    this.orderNo = orderNo;
  }
}