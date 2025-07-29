package com.sinsaimdang.masilkkoon.masil.auth.filter;

import com.sinsaimdang.masilkkoon.masil.auth.util.JwtUtil;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements Filter {

    private final JwtUtil jwtUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("JWT 인증 필터 초기화 완료");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        log.debug("JWT 필터 처리 시작 - {}, {}", method, requestURI);

        // 1. 인증이 필요하지 않은 기능들에 대해서는 필터를 통한 검증을 건너뜀
        if(isPublicPath(requestURI)) {
            log.debug("공개 경로 접근 - 인증 생략: {}", requestURI);
            chain.doFilter(request, response);
            return;
        }

        // 2. OPTIONS 요청은 CORS preflight 요청이므로 검증 없이 통과
        if ("OPTIONS".equals(method)) {
            log.debug("OPTIONS 요청 - 인증 건너뛰기");
            chain.doFilter(request, response);
            return;
        }

        // 3. 토큰 검증이 필요한 경우

        try {
            String token = extractTokenFromHeader(httpRequest);

            if(token == null) {
                log.warn("JWT 토큰이 없습니다 : {}", requestURI);
                sendUnauthorizedResponse(httpResponse, "인증 토큰이 필요합니다.");
                return;
            }

            if(!validateTokenAndSetAttributes(token, httpRequest)) {
                log.warn("유효하지 않은 JWT 토큰 : {}", requestURI);
                sendUnauthorizedResponse(httpResponse, "유효하지 않은 토큰입니다");
                return;
            }

            // 검증된 사용자 정보를 요청 속성에 저장
            Long userId = (Long) httpRequest.getAttribute("currentUserId");
            String userEmail = (String) httpRequest.getAttribute("currentUserEmail");

            log.debug("JWT 인증 성공 - 이메일 = {}, 아이디 = {}", userEmail, userId);

            chain.doFilter(request, response);

        } catch (Exception e) {
            log.error("JWT 필터 처리 중 오류 발생 - {} : {}", requestURI, e.getMessage());
            sendUnauthorizedResponse(httpResponse, "인증 처리 중 오류가 발생했습니다");
        }
    }

    // 공개 경로인지 확인
    private boolean isPublicPath(String requestURI) {
        System.out.println("=== 요청 URI 체크: " + requestURI + " ==="); // 임시 로그
        // 공개 경로 목록 (인증 없이 접근 가능한 목록)
        String[] publicPaths = {
                "/api/auth/signup",
                "/api/auth/login",
                "/api/auth/refresh",
                "/api/auth/check-email",
                "/api/auth/check-nickname",
                "/api/articles",
        };

        for (String publicPath : publicPaths) {
            if (requestURI.startsWith(publicPath)) {
                System.out.println("=== 공개 경로 매칭: " + publicPath + " ==="); // 임시 로그
                return true;
            }
        }

        return false;
    }

    // HTTP Request에서 JWT 토큰을 추출
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        log.debug("Authorization Header = {}", authorizationHeader);

        // Authorization Header가 없는 경우
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            return null;
        }

        // Bearer 접두사 확인
        if (!authorizationHeader.startsWith("Bearer ")) {
            log.warn("잘못된 Authorization Header 형식 : {}", authorizationHeader);
            return null;
        }

        // Bearer 이후의 토큰 부분 추출
        String token = authorizationHeader.substring(7);

        if(token.trim().isEmpty()) {
            log.warn("Authorization Header에 토큰이 없습니다");
            return null;
        }

        return token;
    }

    private boolean validateTokenAndSetAttributes(String token, HttpServletRequest request) {
        try {
            Long userId = jwtUtil.extractUserId(token);
            String userEmail = jwtUtil.extractUserEmail(token);
            String userName = jwtUtil.extractUserName(token);
            String userNickname = jwtUtil.extractUserNickname(token);
            String userRole = jwtUtil.extractUserRole(token);

            if (userId == null || userEmail == null) {
                log.warn("토큰에서 이메일 또는 Id를 찾을 수 없음");
                return false;
            }

            request.setAttribute("currentUserId", userId);
            request.setAttribute("currentUserEmail", userEmail);
            request.setAttribute("currentUserName", userName);
            request.setAttribute("currentUserNickname", userNickname);
            request.setAttribute("currentUserRole", userRole);

            log.debug("토큰 검증 성공 : Email = {}, ID = {}", userEmail, userId);
            return true;
        } catch (Exception e) {
            log.warn("JWT 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    // 인증 실패시 401 Unauthorized Response 전송
    private void sendUnauthorizedResponse(HttpServletResponse response, String message)
        throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{\"success\": false, \"message\": \"%s\", \"timestamp\": %d}",
                message,
                System.currentTimeMillis()
        );

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();

        log.debug("401 Unauthorized 응답 전송 : {}", message);
    }

    @Override
    public void destroy() {
        log.info("JWT 인증 필터 종료");
    }
}
