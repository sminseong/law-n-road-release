package com.lawnroad.template.dto.order;

import lombok.Data;

@Data
public class ClientOrderTemplateListRequestDto {
  private Long orderNo;
  private String templateType; // FILE, EDITOR
  private String categoryName;
  private Boolean isDownloaded;
  private int page;
  private int limit;
}