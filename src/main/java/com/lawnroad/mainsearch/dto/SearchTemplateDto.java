package com.lawnroad.mainsearch.dto;

import com.google.api.client.util.DateTime;
import lombok.Data;

@Data
public class SearchTemplateDto {
  private Long no;
  private Long userNo;
  private Long categoryNo;
  private String type;
  private String name;
  private String description;
  private Integer price;
  private String thumbnailPath;
  private Integer discountRate;
}
