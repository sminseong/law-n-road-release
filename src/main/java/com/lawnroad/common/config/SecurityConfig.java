package com.lawnroad.common.config;

import com.lawnroad.account.Oauth2.OAuth2SuccessHandler;
import com.lawnroad.account.security.JwtAuthenticationFilter;
import com.lawnroad.account.service.CustomOAuth2UserService;
import com.lawnroad.common.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정
                .cors(withDefaults())
                // CSRF 비활성화 (JWT stateless)
                .csrf(csrf -> csrf.disable())
                // 세션을 사용하지 않음
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 권한 및 공개 엔드포인트 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 허용
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/public/**",
                                "/login/oauth2/**",
                                "/oauth2/**",
                                "/api/find-id",
                                "/api/reset-password",
                                "/mail/**",
                                "/api/user/**",
                                "/api/auth/nickname",
                                "/api/notification/**",
                                "/uploads/**",
                                "/api/webhook/**",
                                "/api/signuplawyer"
                        ).permitAll()
                        // 리프레시 토큰 엔드포인트
                        .requestMatchers("/api/refresh").permitAll()
                        // 정적 리소스
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()
                        // 역할 기반 접근 제어
                        .requestMatchers("/api/client/**").hasRole("CLIENT")
                        .requestMatchers("/api/lawyer/**").hasRole("LAWYER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                // OAuth2 로그인 성공 핸들러
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(ui -> ui.userService(customOAuth2UserService))
                        .successHandler((AuthenticationSuccessHandler) oAuth2SuccessHandler)
                )
                // JWT 필터 등록
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 정책 전역 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.addAllowedOriginPattern("*");
        cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.addAllowedHeader("*");
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    // AuthenticationManager 빈 (필요 시)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ※ PasswordEncoder 빈은 AppConfig 클래스에 이미 정의되어 있으므로 여기서는 제거했습니다.
}
