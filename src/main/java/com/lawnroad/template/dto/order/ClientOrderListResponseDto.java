package com.lawnroad.template.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class ClientOrderListResponseDto {
  private List<ClientOrderListDto> orders;
  private int totalPages;
}