package com.lawnroad.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Gemini API 키 설정을 application.properties에서 불러오는 설정 클래스
 * application.properties 예시:
 * gemini.api-key=${GEMINI_API_KEY}
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "gemini")
public class GeminiConfig {
  private String apiKey;
  
}