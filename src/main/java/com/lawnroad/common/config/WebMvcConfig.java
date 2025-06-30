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
  
  // spring.profiles.active 값이 있으면 그걸 쓰고,
  // 없으면 빈 문자열 ""을 기본값으로 사용해라.
  @Value("${spring.profiles.active:}")
  private String activeProfile;
  

  // ✅ CORS 에러애 대해 -> proxy로 해결하는 클래스
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // spring.profiles.active가 없거나 비어 있으면 prod로 간주
    boolean isProd = "prod".equals(activeProfile);
    
    String allowedOrigin = isProd
        ? "https://lawnroad.kr"
        : "http://localhost:5173";
    
    System.out.println( "allowedOrigin -> " + allowedOrigin);
    
    // 어떤 URL에 대해 어디서 요청을 허용할지 설정
    registry.addMapping("/mail/**")
            .allowedOrigins(allowedOrigin) // 어디에서 온 요청을 허용
            .allowedMethods("GET", "POST", "OPTIONS") // ✅ OPTIONS 추가!
            .allowedHeaders("*") // 모든 헤더 허용
            .allowCredentials(false);

    registry.addMapping("/api/**")
            .allowedOrigins(allowedOrigin)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*") // 모든 헤더 허용
            .allowCredentials(false);

    registry.addMapping("/**")
            .allowedOrigins(allowedOrigin) // Vue 개발 서버 주소
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
