package com.lawnroad.ai.controller;

import com.lawnroad.ai.dto.InterviewChatRequestDto;
import com.lawnroad.ai.dto.InterviewChatResponseDto;
import com.lawnroad.ai.dto.TextToneFixRequestDto;
import com.lawnroad.ai.dto.TextToneFixResponseDto;
import com.lawnroad.ai.service.TextToneFixService;
import com.sun.source.doctree.DocCommentTree;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 텍스트 말투 교정 컨트롤러
 * 사용자의 문장을 요청된 말투에 맞춰 교정하여 반환한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gemini")
public class TextToneFixController {
  
  private final TextToneFixService textToneFixService;
  
  /**
   * 문장을 지정된 톤(toneKey)에 맞게 교정
   *
   * @param dto TextToneFixRequestDto { text, tone }
   * @return TextToneFixResponseDto { fixed }
   */
  @PostMapping("/fix-tone")
  public ResponseEntity<TextToneFixResponseDto> fixTone(@RequestBody TextToneFixRequestDto dto) {
    String fixed = textToneFixService.fixTextTone(dto.getText(), dto.getTone());
    return ResponseEntity.ok(new TextToneFixResponseDto(fixed));
  }
  @PostMapping("/interview")
  public ResponseEntity<InterviewChatResponseDto> chat(@RequestBody InterviewChatRequestDto dto) {
    
    InterviewChatResponseDto response = textToneFixService.generateResponse(dto);
    
    // AI: 제거
    response.setReply(
        response.getReply().replaceAll("(?m)^AI:\\s*", "").trim()
    );
    
    return ResponseEntity.ok(response);
  }
  
}