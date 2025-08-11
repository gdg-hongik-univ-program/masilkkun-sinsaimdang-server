package com.sinsaimdang.masilkkoon.masil.auth.controller;

import com.sinsaimdang.masilkkoon.masil.auth.dto.request.AccessTokenRefreshRequest;
import com.sinsaimdang.masilkkoon.masil.auth.dto.request.LoginRequest;
import com.sinsaimdang.masilkkoon.masil.auth.dto.request.LogoutRequest;
import com.sinsaimdang.masilkkoon.masil.auth.dto.request.SignupRequest;
import com.sinsaimdang.masilkkoon.masil.auth.dto.response.SignupResponse;
import com.sinsaimdang.masilkkoon.masil.auth.service.AuthService;
import com.sinsaimdang.masilkkoon.masil.common.util.ApiResponseUtil;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Map;

/**<br>
 * 인증 관련 엔드포인트<br>
 * 1. 회원가입 : POST /api/auth/signup<br>
 * 2. 로그인 : POST /api/auth/login<br>
 * 3. 로그아웃 : POST /api/auth/logout<br>
 * 4. Access Token 갱신 : POST /api/auth/refresh<br>
 * 5. 이메일 중복 확인 : GET /api/auth/check-email<br>
 * 6. 닉네임 중복 확인 : GET /api/auth/check-nickname<br>
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
        log.info("API REQ >> POST /api/auth/signup | 요청 이메일: {}", request.getEmail());

        User user = authService.signup(request.getEmail(), request.getPassword(), request.getName(), request.getNickname());
        SignupResponse responseDto = SignupResponse.from(user);

        log.info("API RES >> POST /api/auth/signup | 요청 이메일: {}", request.getEmail());
        return ApiResponseUtil.created("회원가입에 성공하였습니다.", responseDto);
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
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        log.info("API REQ >> POST /api/auth/login | 요청 이메일: {}", request.getEmail());

        Map<String, String> tokens = authService.login(request.getEmail(), request.getPassword());

        log.info("API RES >> POST /api/auth/login | 요청 이메일: {}", request.getEmail());
        return ApiResponseUtil.success("로그인에 성공했습니다", tokens);
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
        log.info("API REQ >> POST /api/auth/logout | 요청 이메일: {}", request.getEmail());

        authService.logout(request.getEmail());

        log.info("API RES >> POST /api/auth/logout | 요청 이메일: {}", request.getEmail());
        return ApiResponseUtil.success("로그아웃에 성공했습니다");
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
    public ResponseEntity<Map<String, Object>> refreshToken(@Valid @RequestBody AccessTokenRefreshRequest request) {
        log.info("API REQ >> POST /api/auth/refresh");

        Map<String, String> tokens = authService.refreshAccessToken(request.getRefreshToken());

        log.info("API RES >> POST /api/auth/refresh");
        return ApiResponseUtil.success("토큰 갱신이 완료되었습니다.", tokens);
    }

    // 이메일 중복 확인
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmailDuplicate(@RequestParam String email) {
        log.info("API REQ >> GET /api/auth/check-email | email={}", email);

        boolean exists = authService.isEmailExists(email);

        log.info("API RES >> GET /api/auth/check-email | email={}", email);
        return ApiResponseUtil.success("이메일 중복 확인 완료", Map.of("exists", exists, "available", !exists));
    }

    // 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Object>> checkNicknameDuplicate(@RequestParam String nickname) {
        log.info("API REQ >> GET /api/auth/check-nickname | nickname={}", nickname);

        boolean exists = authService.isNicknameExists(nickname);

        log.info("API RES >> GET /api/auth/check-nickname | nickname={}", nickname);
        return ApiResponseUtil.success("닉네임 중복 확인 완료", Map.of("exists", exists, "available", !exists));
    }
}
