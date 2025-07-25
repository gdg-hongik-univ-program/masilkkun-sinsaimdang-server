package com.sinsaimdang.masilkkoon.masil.common.config;

import com.sinsaimdang.masilkkoon.masil.auth.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security를 활성화합니다.
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 보호 비활성화 (Stateless API는 CSRF 보호가 필요 없음)
                .csrf(csrf -> csrf.disable())

                // 2. 기본 로그인/로그아웃, HTTP Basic 인증 비활성화
                .formLogin(formLogin -> formLogin.disable())
                .logout(logout -> logout.disable())
                .httpBasic(httpBasic -> httpBasic.disable())

                // 3. 세션을 사용하지 않는 Stateless 서버로 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. API 경로별 접근 권한 설정
                .authorizeHttpRequests(authz -> authz
                        // '/api/auth/**' 경로는 인증 없이 누구나 접근 허용
                        .requestMatchers(
                                "/api/auth/**",
                                "/error" // 에러 페이지도 열어줘야 함
                        ).permitAll()
                        // 그 외의 모든 요청은 반드시 인증을 거쳐야 함
                        .anyRequest().authenticated()
                )

                // 5. 우리가 만든 JWT 필터를 Spring Security 필터 체인에 추가
                // (UsernamePasswordAuthenticationFilter보다 먼저 실행되도록 설정)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}