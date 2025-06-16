package com.lawnroad.template.dto.order;

import lombok.Data;

@Data
public class ClientOrderTemplateDto {
  private Long tmplNo;
  private String templateName;
  private String templateType;     // FILE or EDITOR
  private String categoryName;
  private Integer price;           // 실제 구매 당시 할인 적용 가격
  private Boolean isDownloaded;
  private String thumbnailPath;
}