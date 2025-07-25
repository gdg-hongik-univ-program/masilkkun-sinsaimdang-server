package com.sinsaimdang.masilkkoon.masil.auth.filter;

import com.sinsaimdang.masilkkoon.masil.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
// Filter 대신 OncePerRequestFilter를 상속받아 요청당 한 번만 실행되도록 보장합니다.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 1. 토큰 검증이 필요 없는 경로는 바로 통과
        // SecurityConfig에서 이미 설정했지만, 필터 레벨에서 한 번 더 체크하면 명확합니다.
        if (isPublicPath(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 헤더에서 토큰 추출
        String token = extractTokenFromHeader(request);

        if (token == null) {
            log.warn("JWT 토큰이 없습니다 : {}", requestURI);
            sendUnauthorizedResponse(response, "인증 토큰이 필요합니다.");
            return;
        }

        try {
            // 3. 토큰 검증
            if (jwtUtil.extractUserEmail(token) != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 토큰에서 사용자 정보 추출
                String userEmail = jwtUtil.extractUserEmail(token);
                String role = jwtUtil.extractUserRole(token);
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

                // 4. Spring Security가 이해할 수 있는 Authentication 객체 생성
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userEmail, null, authorities);

                // 5. SecurityContextHolder에 인증 정보 저장 (가장 중요한 부분!)
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.debug("Security Context에 인증 정보 저장 완료: {}, 권한: {}", userEmail, authorities);

                // 추가: request attribute에도 사용자 정보 저장 (기존 로직 호환성 유지)
                request.setAttribute("currentUserId", jwtUtil.extractUserId(token));
                request.setAttribute("currentUserEmail", userEmail);
            }
        } catch (Exception e) {
            log.warn("유효하지 않은 JWT 토큰: {}", e.getMessage());
            SecurityContextHolder.clearContext(); // 컨텍스트 정리
            sendUnauthorizedResponse(response, "유효하지 않은 토큰입니다.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("{\"success\": false, \"message\": \"%s\"}", message));
    }

    // SecurityConfig와 중복되지만, 필터 독립성을 위해 유지
    private boolean isPublicPath(String requestURI) {
        return requestURI.startsWith("/api/auth/");
    }
}