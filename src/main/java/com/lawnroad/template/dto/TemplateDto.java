package com.lawnroad.template.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TemplateDto {
  private Long no;
  private Long user_no;
  private Long category_no;
  private String category_name;
  private String name;
  private String description;
  private Integer price;
  private String template_path;
  private String thumbnail_path;
  private Integer sales_count;
  private Integer discount_rate;
  private LocalDateTime created_at;
  private LocalDateTime updated_at;
}