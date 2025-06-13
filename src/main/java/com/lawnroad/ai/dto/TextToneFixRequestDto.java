package com.lawnroad.ai.dto;

import lombok.Data;

@Data
public class TextToneFixRequestDto {
  // 교정 대상 텍스트
  private String text;
  // 적용할 교정 유형
  private String tone;
}