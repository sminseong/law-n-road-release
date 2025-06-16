package com.lawnroad.template.dto.cart;

import lombok.Data;

@Data
public class CartAddRequestDto {
  private Long tmplNo;  // 담을 템플릿 번호만 있으면 충분
}