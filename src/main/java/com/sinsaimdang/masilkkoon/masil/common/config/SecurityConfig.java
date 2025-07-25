package com.sinsaimdang.masilkkoon.masil.common.config;

import com.sinsaimdang.masilkkoon.masil.auth.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
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
                // 1. CORS 설정을 Spring Security에 통합합니다.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. CSRF 보호 비활성화 (Stateless API는 CSRF 보호가 필요 없음)
                .csrf(AbstractHttpConfigurer::disable)

                // 3. 기본 로그인/로그아웃, HTTP Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 4. 세션을 사용하지 않는 Stateless 서버로 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5. API 경로별 접근 권한 설정
                .authorizeHttpRequests(authz -> authz
                        // '/api/auth/**' 경로는 인증 없이 누구나 접근 허용
                        .requestMatchers(
                                "/api/auth/**",
                                "/error" // 에러 페이지도 열어줘야 함
                        ).permitAll()
                        // 그 외의 모든 요청은 반드시 인증을 거쳐야 함
                        .anyRequest().authenticated()
                )

                // 6. 우리가 만든 JWT 필터를 Spring Security 필터 체인에 추가
                // (UsernamePasswordAuthenticationFilter보다 먼저 실행되도록 설정)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 설정을 위한 Bean을 추가합니다.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        // 프론트엔드 개발 서버 주소를 허용합니다.
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001", "http://localhost:5173", "http://localhost:5174"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        // 프론트엔드에서 Authorization 헤더를 사용할 수 있도록 노출시킵니다.
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 위 설정을 적용합니다.
        return source;
    }
}