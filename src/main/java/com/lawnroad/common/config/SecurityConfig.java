package com.lawnroad.common.config;
import com.lawnroad.account.security.JwtAuthenticationFilter;
import com.lawnroad.common.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenUtil jwtTokenUtil;
  
  
  
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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/**",
                                "/mail/**",
                                "/uploads/**",
                                "/api/client/*/reservations/counts",
                                "/api/client/**/reservations"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }







//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http.csrf().disable()
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil), UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }

}
