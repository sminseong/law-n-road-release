package com.lawnroad.template.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TemplateListDto {
  private Long no;
  private String category_name;
  private String name;
  private Integer price;
  private Integer discount_rate;
  private Integer sales_count;
  private LocalDateTime created_at;
  private String thumbnail_path;
}