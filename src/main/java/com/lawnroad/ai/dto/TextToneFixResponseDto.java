package com.lawnroad.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TextToneFixResponseDto {
  // 교정 결과 문장
  private String fixed;
}