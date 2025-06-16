package com.lawnroad.ai.service;

import com.lawnroad.ai.dto.InterviewChatRequestDto;
import com.lawnroad.ai.dto.InterviewChatResponseDto;

public interface TextToneFixService {
  String fixTextTone(String text, String toneKey);
  
  
  String generateReply(InterviewChatRequestDto dto);
  InterviewChatResponseDto generateResponse(InterviewChatRequestDto dto);
}
