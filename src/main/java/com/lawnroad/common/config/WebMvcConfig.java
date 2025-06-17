package com.lawnroad.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
  
  @Value("${file.upload-dir}")
  private String uploadDir;

  // ✅ CORS 에러애 대해 -> proxy로 해결하는 클래스
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // 어떤 URL에 대해 어디서 요청을 허용할지 설정
    registry.addMapping("/mail/**")
        .allowedOrigins("http://localhost:5173") // 어디에서 온 요청을 허용
        .allowedMethods("GET", "POST", "OPTIONS") // ✅ OPTIONS 추가!
        .allowedHeaders("*") // 모든 헤더 허용
        .allowCredentials(false);

    registry.addMapping("/api/**")
        .allowedOrigins("http://localhost:5173")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*") // 모든 헤더 허용
        .allowCredentials(false);

    registry.addMapping("/**")
        .allowedOrigins("http://localhost:5173") // Vue 개발 서버 주소
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true);
  }
  
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/uploads/**") // 요청 경로
        .addResourceLocations(uploadDir); // 실제 파일 경로
  }
  
}

