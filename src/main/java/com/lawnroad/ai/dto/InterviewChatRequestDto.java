package com.lawnroad.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class InterviewChatRequestDto {
  private String content; // 템플릿 본문 (#{변수} 포함)
  private String description; // 템플릿 설명
  private List<VariableDto> variables; // 질문해야 할 변수 리스트
  private List<MessageDto> history; // 대화 이력
}