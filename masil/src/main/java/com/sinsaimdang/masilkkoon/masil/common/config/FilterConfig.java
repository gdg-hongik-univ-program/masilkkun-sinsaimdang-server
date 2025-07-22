package com.sinsaimdang.masilkkoon.masil.common.config;

import com.sinsaimdang.masilkkoon.masil.auth.filter.JwtAuthenticationFilter;
import com.sinsaimdang.masilkkoon.masil.auth.util.JwtUtil;
import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class FilterConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtUtil jwtUtil,
            UserRepository userRepository) {
        return new JwtAuthenticationFilter(jwtUtil, userRepository);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean =
                new FilterRegistrationBean<>();

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

        config.setAllowCredentials(true);

        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:3001");
        config.addAllowedOrigin("http://127.0.0.1:3000");

        config.addAllowedMethod("GET");     // 조회
        config.addAllowedMethod("POST");    // 생성
        config.addAllowedMethod("PUT");     // 수정
        config.addAllowedMethod("DELETE");  // 삭제
        config.addAllowedMethod("OPTIONS"); // CORS preflight 요청
        config.addAllowedMethod("PATCH");   // 부분 수정

        config.addAllowedHeader("*");       // 모든 헤더 허용

        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Type");
        config.addExposedHeader("X-Total-Count");

        config.setMaxAge(3600L); // 1시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로에 적용

        FilterRegistrationBean<CorsFilter> registrationBean =
                new FilterRegistrationBean<>(new CorsFilter(source));

        registrationBean.setOrder(0);
        registrationBean.setName("corsFilter");

        return registrationBean;
    }
}