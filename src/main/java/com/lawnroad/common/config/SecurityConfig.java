package com.lawnroad.common.config;
import com.lawnroad.account.security.JwtAuthenticationFilter;
import com.lawnroad.common.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenUtil jwtTokenUtil;

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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/public/**", "/mail/**","/api/auth/lawyer_signup" ,"/uploads/**").permitAll()
                        .requestMatchers("/api/client/**").hasRole("CLIENT")
                        .requestMatchers("/api/lawyer/**").hasRole("LAWYER")
                        .anyRequest().authenticated()
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
