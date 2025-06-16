package com.lawnroad.ai.service;

import org.springframework.stereotype.Component;

@Component
public class DocumentGenerator {
  
  // 그냥 줄바꿈 보존 + 기본 스타일로 감싸기
  public String wrapAsHtml(String content) {
    return "<html><body style=\"font-family:inherit; white-space:pre-wrap; line-height:1.6;\">" +
        content +
        "</body></html>";
  }
}