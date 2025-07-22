package com.sinsaimdang.masilkkoon.masil.user.controller;

import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpServletRequest request) {
        log.info("현재 사용자 정보 조회 요청");

        try {
            // JWT 필터에서 설정한 사용자 정보 추출
            User currentUser = (User) request.getAttribute("currentUser");
            Long userId = (Long) request.getAttribute("currentUserId");
            String userEmail = (String) request.getAttribute("currentUserEmail");

            // 사용자 정보가 없는 경우 (필터에서 인증 실패)
            if (currentUser == null) {
                log.warn("인증되지 않은 사용자의 정보 조회 시도");

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "인증되지 않은 사용자입니다.");

                return ResponseEntity.status(401).body(errorResponse);
            }

            // 성공 응답 생성
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", currentUser.getId());
            userData.put("email", currentUser.getEmail());
            userData.put("name", currentUser.getName());
            userData.put("nickname", currentUser.getNickname());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "사용자 정보 조회 성공");
            response.put("data", userData);

            log.info("사용자 정보 조회 성공 - ID: {}, Email: {}",
                    currentUser.getId(), currentUser.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류 발생", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "서버 내부 오류가 발생했습니다.");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(
            @RequestAttribute(value = "currentUser", required = false) User user) {

        log.info("사용자 프로필 조회 요청");

        if (user == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "인증이 필요합니다.");

            return ResponseEntity.status(401).body(errorResponse);
        }

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("id", user.getId());
        profileData.put("email", user.getEmail());
        profileData.put("name", user.getName());
        profileData.put("nickname", user.getNickname());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "프로필 조회 성공");
        response.put("data", profileData);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testAuth(
            @RequestAttribute(value = "currentUserId", required = false) Long userId) {

        log.info("JWT 인증 테스트 요청");

        if (userId == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "인증 실패");

            return ResponseEntity.status(401).body(errorResponse);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "JWT 인증 성공!");
        response.put("data", Map.of(
                "userId", userId,
                "timestamp", System.currentTimeMillis()
        ));

        log.info("JWT 인증 테스트 성공 - 사용자 ID: {}", userId);

        return ResponseEntity.ok(response);
    }
}