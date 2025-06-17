package com.lawnroad.ai.service;

import com.lawnroad.ai.dto.InterviewChatRequestDto;
import com.lawnroad.ai.dto.InterviewChatResponseDto;
import com.lawnroad.ai.dto.ValidationResultDto;

public interface AiService {
  // 맞춤법 교정, 말투 수정
  String fixTextTone(String text, String toneKey);
  
  // ai 인터뷰
  InterviewChatResponseDto generateResponse(InterviewChatRequestDto dto);
  
  // ai 문서 검증
  ValidationResultDto validateTemplateContent(String content, String name, String description);
}
