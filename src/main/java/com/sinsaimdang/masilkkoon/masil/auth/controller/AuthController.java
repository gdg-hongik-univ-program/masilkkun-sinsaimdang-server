package com.sinsaimdang.masilkkoon.masil.auth.controller;

import com.sinsaimdang.masilkkoon.masil.auth.dto.AccessTokenRefreshRequest;
import com.sinsaimdang.masilkkoon.masil.auth.dto.LoginRequest;
import com.sinsaimdang.masilkkoon.masil.auth.dto.LogoutRequest;
import com.sinsaimdang.masilkkoon.masil.auth.dto.SignupRequest;
import com.sinsaimdang.masilkkoon.masil.auth.service.AuthService;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


import java.util.HashMap;
import java.util.Map;

/**
 * 인증 관련 엔드포인트
 * 1. 회원가입 : POST /api/auth/signup
 * 2. 로그인 : POST /api/auth/login
 * 3. 로그아웃 : POST /api/auth/logout
 * 4. Access Token 갱신 : POST /api/auth/refresh
 * 5. 이메일 중복 확인 : GET /api/auth/check-email
 * 6. 닉네임 중복 확인 : GET /api/auth/check-nickname
 */
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Slf4j
@ComponentScan
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입<br>
     * 과정에서 수행되는 검증 <br>
     * 1. 모든 필드 존재, 길이 ,형식 검증 <br>
     * 2. 이메일 & 닉네임 중복 검사 <br>
     * 3. 비밀번호 보안 정책 검사 <br>
     * @param request 회원가입 요청 데이터
     * @return 성공시 201 Created + 생성된 사용자 정보
     * @throws IllegalArgumentException 입력값이 유효하지 않거나 중복되는 이메일, 닉네임
     * @throws SecurityException 보안정책 위반시
     * @see AuthService#signup(String, String, String, String)
     * @see SignupRequest
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody SignupRequest request) {
        log.info("회원가입 요청 : 이메일 = {}", request.getEmail());

        try {
            User user = authService.signup(
                    request.getEmail(),
                    request.getPassword(),
                    request.getName(),
                    request.getNickname()
            );

            // 회원가입 성공 Response 생성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회원가입에 성공하였습니다.");
            response.put("data", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "nickname", user.getNickname()
            ));

            log.info("회원가입 성공 - ID: {}, 이메일: {}", user.getId(), user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException | SecurityException e) {
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

    /**
     * 로그인 <br>
     * @param request 로그인 요청 데이터
     * @return 성공시 200 OK + Access Token + Refresh Token
     * @throws IllegalArgumentException 존재하지 않는 이메일이거나 비밀번호가 일치하지 않는 경우
     * @see AuthService#login(String, String)
     * @see LoginRequest
     */
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

    /**
     * 로그아웃 <br>
     * 해당 사용자의 Refresh Token을 서버의 DB에서 삭제함
     * @param request - 사용자 이메일 정보
     * @return 성공시 200 OK
     * @see AuthService#logout(String)
     * @see LogoutRequest
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@Valid @RequestBody LogoutRequest request) {
        log.info("로그아웃 요청 - 이메일 = {}", request.getEmail());

        try {
            // 이메일 유효성 검증
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                log.warn("로그아웃 실패 - 이메일이 비어있음");

                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "이메일은 필수 항목입니다.");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // 이메일 존재 여부 확인 (선택사항 - 보안상 실제 존재 여부를 노출하지 않을 수도 있음)
            if (!authService.isEmailExists(request.getEmail())) {
                log.warn("로그아웃 실패 - 존재하지 않는 이메일: {}", request.getEmail());

                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "존재하지 않는 이메일입니다.");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            authService.logout(request.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "로그아웃에 성공했습니다");

            log.info("로그아웃 성공 - 이메일: {}", request.getEmail());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("로그아웃 실패 - 이메일: {}, 사유: {}", request.getEmail(), e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            log.error("로그아웃 중 서버 오류 - 이메일: {}", request.getEmail(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 내부 오류가 발생했습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Access Token 갱신<br>
     * Refresh Token 을 사용하여 Access Token을 갱신합니다<br>
     * Access Token 이 만료되었을 때 사용하는 API<br>
     * Refresh Token 또한 만료되었을 경우 재로그인이 필요합니다
     * @param request - RefreshToken
     * @return 성공시 200 OK + 새로운 Access Token + Refresh Token
     */
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
