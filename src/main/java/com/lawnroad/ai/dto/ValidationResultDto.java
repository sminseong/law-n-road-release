package com.lawnroad.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ValidationResultDto {
  private final boolean passed;
  private final List<String> reasons;  // 불합격 사유
}