package com.lawnroad.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
  
  // ✅ CORS 설정 추가
  @Override
  public void addCorsMappings(CorsRegistry registry) {
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
        //.addResourceLocations("file:///D:/Program/final/law-n-road/uploads/"); // 실제 파일 경로 (건희님경로)
       // .addResourceLocations("file:///C:/study/law-n-road/uploads/");
        .addResourceLocations("file:///C:\\study\\law-n-road\\uploads");
  }
  
}

