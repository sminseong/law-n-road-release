package com.lawnroad.common.config;

import com.lawnroad.account.security.JwtAuthenticationFilter;
import com.lawnroad.common.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenUtil jwtTokenUtil;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1) 비회원에게 허용
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/public/**",
                                "/api/find-id",
                                "/api/reset-password",
                                "/mail/**",
                                "/api/user/**",
                                "/api/auth/nickname",
                                "/api/notification/**",
                                "/uploads/**",
                                "/api/webhook/**",
                                "/ws/**"
                        ).permitAll()

                        // 2) AI 및 슬롯 조회는 CLIENT 또는 LAWYER 권한 모두 허용
                        .requestMatchers("/api/ai/**", "/api/lawyer/*/slots", "/api/confirm/payment","/api/confirm/cancel")
                        .hasAnyRole("CLIENT", "LAWYER")

                        // 3) 클라이언트 전용 API
                        .requestMatchers("/api/client/**")
                        .hasRole("CLIENT")

                        // 4) 변호사 전용 API
                        .requestMatchers("/api/lawyer/**")
                        .hasRole("LAWYER")

                        // 5) 그 외 모든 요청은 인증만 되어 있으면 OK
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
