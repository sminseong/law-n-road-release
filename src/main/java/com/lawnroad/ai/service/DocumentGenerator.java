package com.lawnroad.ai.service;

import org.springframework.stereotype.Component;

@Component
public class DocumentGenerator {
  
  // 텍스트 문서를 HTML <pre>로 감싸기
  public String wrapAsHtml(String content) {
    return "<html><body><pre style=\"font-family:inherit; white-space:pre-wrap;\">"
        + escapeHtml(content) +
        "</pre></body></html>";
  }
  
  // 특수 문자 이스케이프 처리 (보안용)
  private String escapeHtml(String input) {
    return input
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;");
  }
}