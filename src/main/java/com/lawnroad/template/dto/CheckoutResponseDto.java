package com.lawnroad.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutResponseDto {
  private Long orderNo;
  // (필요하다면 결제 트랜잭션 ID, 결제금액, 타임스탬프 등 추가)
}