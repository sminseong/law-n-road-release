package com.lawnroad.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MonthlyRevenueDto {
  private String month;
  private Long totalAmount;
}
