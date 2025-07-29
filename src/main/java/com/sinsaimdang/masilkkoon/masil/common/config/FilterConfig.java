package com.sinsaimdang.masilkkoon.masil.common.config;

import com.sinsaimdang.masilkkoon.masil.auth.filter.JwtAuthenticationFilter;
import com.sinsaimdang.masilkkoon.masil.auth.util.JwtUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class FilterConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean =
                new FilterRegistrationBean<>();

        registrationBean.setFilter(jwtAuthenticationFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);  // CORS 필터 다음에 실행

        // 등록할 필터 설정
        registrationBean.setFilter(jwtAuthenticationFilter);

        // 필터가 적용될 URL 패턴 설정
        registrationBean.addUrlPatterns("/*");

        // 필터 실행 순서 설정 (낮은 숫자가 먼저 실행)
        registrationBean.setOrder(1);

        // 필터 이름 설정 (Bean 이름 충돌 방지)
        registrationBean.setName("jwtFilterRegistration");

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ 프론트엔드 localhost:5173 명시적 허용
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173");  // Vite 개발 서버
        config.addAllowedOrigin("http://127.0.0.1:5173");
        config.addAllowedOrigin("http://localhost:3000");  // React 기본 포트
        config.addAllowedOrigin("http://127.0.0.1:3000");

        // ✅ 모든 HTTP 메서드 허용
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        config.addAllowedMethod("OPTIONS");  // preflight 요청 필수

        // ✅ 모든 헤더 허용
        config.addAllowedHeader("*");

        // ✅ 응답 헤더 노출
        config.addExposedHeader("*");

        // ✅ preflight 캐시 시간
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // 모든 경로에 적용


        FilterRegistrationBean<CorsFilter> registrationBean =
                new FilterRegistrationBean<>(new CorsFilter(source));

        // ✅ JWT 필터보다 먼저 실행
        registrationBean.setOrder(-1);

        registrationBean.setName("corsFilter");

        return registrationBean;
    }
}