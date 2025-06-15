package com.lawnroad.template.dto;

import lombok.Data;

@Data
public class CartItemResponseDto {
  private Long tmplNo;
  private String name;
  private String thumbnailPath;
  private Integer price;
  private Integer discountRate;
  private String type;           // FILE or EDITOR
  private String categoryName;
  private String lawyerName;
}