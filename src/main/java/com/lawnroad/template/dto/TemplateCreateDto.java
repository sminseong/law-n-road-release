package com.lawnroad.template.dto;

import lombok.Data;

@Data
public class TemplateCreateDto {
  private Long user_no;
  private Long category_no;
  private String name;
  private String description;
  private Integer price;
  private String template_path;
  private String thumbnail_path;
  private Integer discount_rate;
}