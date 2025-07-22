package com.sinsaimdang.masilkkoon.masil.auth.controller;

import com.sinsaimdang.masilkkoon.masil.auth.dto.AccessTokenRefreshRequest;
import com.sinsaimdang.masilkkoon.masil.auth.dto.LoginRequest;
import com.sinsaimdang.masilkkoon.masil.auth.dto.LogoutRequest;
import com.sinsaimdang.masilkkoon.masil.auth.dto.SignupRequest;
import com.sinsaimdang.masilkkoon.masil.auth.service.AuthService;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody SignupRequest request) {
        log.info("회원가입 요청 : 이메일 = {}", request.getEmail());

        // 이메일 없음
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "이메일은 필수항목입니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        // 비밀번호 없음
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "비밀번호는 필수항목입니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            User user = authService.signup(
                    request.getEmail(),
                    request.getPassword(),
                    request.getName(),
                    request.getNickname()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회원가입에 성공하였습니다.");
            response.put("data", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "nickname", user.getNickname()
            ));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("회원가입 실패 : 이메일 = {}, 사유 = {}", request.getEmail(), e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("회원가입 중 서버 오류 - 이메일: {}", request.getEmail(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 내부 오류가 발생했습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        log.info("로그인 요청 : 이메일 = {}", request.getEmail());

        try {
            Map<String, String> tokens = authService.login(request.getEmail(), request.getPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "로그인에 성공했습니다");
            response.put("data", Map.of(
                    "accessToken", tokens.get("accessToken"),
                    "refreshToken", tokens.get("refreshToken")
            ));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("로그인 실패 - 이메일: {}, 사유: {}", request.getEmail(), e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("로그인 중 서버 오류 - 이메일: {}", request.getEmail(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 내부 오류가 발생했습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestBody LogoutRequest request) {
        log.info("로그아웃 요청 - 이메일 = {}", request.getEmail());

        try {
            authService.logout(request.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "로그아웃에 성공했습니다");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("로그아웃 중 서버 오류 - 이메일: {}", request.getEmail(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 내부 오류가 발생했습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 액세스 토큰 갱신
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody AccessTokenRefreshRequest request) {
        log.info("액세스 토큰 갱신 요청");

        try {
            Map<String, String> tokens = authService.refreshAccessToken(request.getRefreshToken());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "토큰 갱신이 완료되었습니다.");
            response.put("data", Map.of(
                    "accessToken", tokens.get("accessToken"),
                    "refreshToken", tokens.get("refreshToken")
            ));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("토큰 갱신 실패 - 사유: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("토큰 갱신 중 서버 오류", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 내부 오류가 발생했습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 이메일 중복 확인
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmailDuplicate(@RequestParam String email) {
        log.info("이메일 중복 확인 요청 - 이메일 = {}", email);

        try {
            boolean exists = authService.isEmailExists(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "email", email,
                    "exists", exists,
                    "available", !exists
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("이메일 중복 확인 중 서버 오류 - 이메일 = {}, 사유 = {}", email, e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 내부 오류가 발생했습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Object>> checkNicknameDuplicate(@RequestParam String nickname) {
        log.info("닉네임 중복 확인 요청 - 닉네임 = {}", nickname);

        try {
            boolean exists = authService.isNicknameExists(nickname);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "nickname", nickname,
                    "exists", exists,
                    "available", !exists
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("닉네임 중복 확인 중 서버 오류 - 닉네임: {}", nickname, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 내부 오류가 발생했습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
