package com.lawnroad.common.config;

import com.lawnroad.account.Oauth2.OAuth2SuccessHandler;
import com.lawnroad.account.security.JwtAuthenticationFilter;
import com.lawnroad.account.service.CustomOAuth2UserService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    // ✅ JwtAuthenticationFilter를 Bean으로 등록
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenUtil);
    }

//  private final JwtAuthenticationFilter jwtAuthenticationFilter;
//  @Bean
//  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    http
//        .csrf().disable()
//        .authorizeHttpRequests(auth -> auth
//            // 비회원도 접근 허용
//            .requestMatchers("/api/public/**").permitAll()
//            // /api/client/** 경로는 ROLE_CLIENT 권한을 가진 사용자만 접근 가능
//            .requestMatchers("/api/client/**").hasRole("CLIENT")
//            // /api/lawyer/** 경로는 ROLE_LAWYER 권한을 가진 사용자만 접근 가능
//            .requestMatchers("/api/lawyer/**").hasRole("LAWYER")
//            .anyRequest().permitAll()
//        )
//        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//    return http.build();
//  }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http.csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        // 1) 비회원에게 허용
//                        .requestMatchers(
//                                "/api/auth/**",
//                                "/api/public/**",
//                                "/api/find-id",
//                                "/api/reset-password",
//                                "/mail/**",
//                                "/api/user/**",
//                                "/api/auth/nickname",
//                                "/api/notification/**",
//                                "/uploads/**",
//                                "/api/webhook/**",
//                                "/api/signuplawyer" ,"/api/refresh"   // ← 여기에 추가
//                        ).permitAll()
//
//                        // 2) AI 및 슬롯 조회는 CLIENT 또는 LAWYER 권한 모두 허용
//                        .requestMatchers("/api/ai/**", "/api/lawyer/*/slots", "/api/confirm/payment","/api/confirm/cancel")
//                        .hasAnyRole("CLIENT", "LAWYER")
//
//                        // 3) 클라이언트 전용 API
//                        .requestMatchers("/api/client/**")
//                        .hasRole("CLIENT")
//
//                        // 4) 변호사 전용 API
//                        .requestMatchers("/api/lawyer/**")
//                        .hasRole("LAWYER")
//
//                        // 5) 그 외 모든 요청은 인증만 되어 있으면 OK
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/api/auth/**", "/api/public/**", "/api/find-id", "/api/reset-password",
                            "/mail/**", "/api/user/**", "/api/auth/nickname", "/api/notification/**",
                            "/uploads/**", "/api/webhook/**", "/api/signuplawyer",
                            "/login/oauth2/**", "/oauth2/**"
                    ).permitAll()
                    .requestMatchers("/api/ai/**", "/api/lawyer/*/slots", "/api/confirm/payment", "/api/confirm/cancel","/api/refresh")
                    .hasAnyRole("CLIENT", "LAWYER")
                    .requestMatchers("/api/client/**").hasRole("CLIENT")
                    .requestMatchers("/api/lawyer/**").hasRole("LAWYER")
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo
                            .userService(customOAuth2UserService))
                    .successHandler((AuthenticationSuccessHandler) oAuth2SuccessHandler) // ✅ 소셜 로그인 성공 처리
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
}




    // ✅ AuthenticationManager Bean (필요한 경우 로그인 처리용)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
