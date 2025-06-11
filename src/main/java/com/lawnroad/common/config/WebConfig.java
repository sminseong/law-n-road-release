package com.lawnroad.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer { //  WebMvcConfigurer  웹 관련 설정을 추가하고 싶을 때 구현하는 인터페이스

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/mail/**")
//                .allowedOrigins("http://localhost:5173")
//                .allowedMethods("GET", "POST")
//                .allowCredentials(false);
//
//        registry.addMapping("/api/**")
//                .allowedOrigins("http://localhost:5173")
//                .allowedMethods("GET", "POST")
//                .allowedHeaders("*")
//                .allowCredentials(false);
//    }

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
    }

}
