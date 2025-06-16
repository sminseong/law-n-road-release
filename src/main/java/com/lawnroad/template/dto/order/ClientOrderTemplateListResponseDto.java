package com.lawnroad.template.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class ClientOrderTemplateListResponseDto {
  private List<ClientOrderTemplateDto> templates;
  private int totalPages;
}