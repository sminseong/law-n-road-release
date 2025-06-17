package com.lawnroad.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InterviewChatResponseDto {
  private String reply; // Gemini가 생성한 응답
  private boolean allVariablesFilled; // 모든 변수 수집 여부
  private String finalHtml;           // (선택) 최종 문서 HTML
}