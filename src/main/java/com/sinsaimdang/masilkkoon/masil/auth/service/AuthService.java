package com.sinsaimdang.masilkkoon.masil.auth.service;

import com.sinsaimdang.masilkkoon.masil.auth.entity.RefreshToken;
import com.sinsaimdang.masilkkoon.masil.auth.repository.RefreshTokenRepository;
import com.sinsaimdang.masilkkoon.masil.auth.util.JwtUtil;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.entity.UserRole;
import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    // 예약어 목록
    private static final String[] RESERVED_NICKNAMES = {
            "admin", "administrator", "관리자", "운영자", "system", "root",
            "user", "guest", "anonymous", "null", "undefined", "test",
            "support", "help", "info", "service", "official"
    };

    // 이름 패턴
    private static final Pattern NAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z\\s]+$");

    // 닉네임 패턴
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9_]+$");

    //회원가입
    @Transactional
    public User signup(String email, String password, String name, String nickname) {
        log.info("회원가입 시도 : 이메일 = {}", email);

        // 입력값 전처리
        NormalizedSignupData data = new NormalizedSignupData(email, password, name, nickname);

        // 중복 검사
        checkDuplicates(data.getEmail(), data.getNickname());

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 생성
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .nickname(nickname)
                .build();

        User savedUser = userRepository.save(user);
        log.info("회원가입 완료 : ID : {} / Email : {}", savedUser.getId(), savedUser.getEmail());

        return savedUser;
    }

    // 로그인
    @Transactional
    public Map<String, String> login(String email, String password) {
        log.info("로그인 시도 : 이메일 {}", email);

        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 이메일로 로그인을 시도했습니다 : {}", email);
                    return new IllegalArgumentException("잘못된 이메일 또는 비밀번호 입니다");
                });

        // 비밀번호 검증
        if(! passwordEncoder.matches(password, user.getPassword())) {
            log.warn("잘못된 비밀번호로 로그인을 시도했습니다 : {}", email);
            throw new IllegalArgumentException("잘못된 이메일 또는 비밀번호 입니다");
        }

        // 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                UserRole.USER.name()
        );
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        // 기존 Refresh Token 무효화 (삭제)
        refreshTokenRepository.deleteByEmail(email);
        saveRefreshToken(email, refreshToken);

        log.info("로그인 성공 : ID = {}, 이메일 = {}", user.getId(), user.getEmail());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    // 로그아웃
    @Transactional
    public void logout(String email){
        log.info("로그아웃 시도 : 이메일 = {}", email);

        refreshTokenRepository.deleteByEmail(email);

        log.info("로그아웃 완료 : 이메일 = {}", email);
    }

    // 액세스 토큰 갱신
    @Transactional
    public Map<String, String> refreshAccessToken(String refreshToken) {
        log.info("토큰 갱신 시도");

        // 리프레시 토큰 조회
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    log.warn("토큰 갱신 실패 : 유효하지 않은 리프레시 토큰");
                    return new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다");
                });

        // 리프레시 토큰 만료 확인
        if (tokenEntity.isExpired()) {
            log.warn("토큰 갱신 실패 : 만료된 리프레시 토큰, 이메일 = {}", tokenEntity.getEmail());
            throw new IllegalArgumentException("만료된 리프레시 토큰입니다");
        }

        // 사용자 조회
        User user = userRepository.findByEmail(tokenEntity.getEmail())
                .orElseThrow(() -> {
                    log.warn("토큰 갱신 실패 : 존재하지 않는 사용자, 이메일 = {}", tokenEntity.getEmail());
                    return new IllegalArgumentException("존재하지 않는 이메일입니다");
                });

        // 새로운 토큰 생성
        String newAccessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                UserRole.USER.name());

        log.info("액세스 토큰 갱신 성공 : ID = {}, Email = {}", user.getId(), user.getEmail());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    // 이메일 중복 확인
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // 닉네임 중복 확인
    public boolean isNicknameExists(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 리프레시 토큰 저장
    private void saveRefreshToken(String email, String token) {
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpiration/1000);
        LocalDateTime now = LocalDateTime.now();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .email(email)
                .expiresAt(expiresAt)
                .createdAt(now)
                .build();

        refreshTokenRepository.save(refreshToken);
        log.debug("리프레시 토큰 저장 완료 : 이메일 = {}", email);
    }

    // 만료 리프레시 토큰 삭제
    @Transactional
    public void deleteExpiredRefreshTokens() {
        log.info("만료된 리프레시 토큰 삭제 시작");
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("만료된 리프레시 토큰 삭제 완료");
    }

    //========== 전처리 메서드 ==========

    // SignUp Request 전처리
    private String[] normalizeSignUpData(String email, String password, String name, String nickname) {
        String normalizedEmail = email != null ? email.toLowerCase().trim() : null;
        String normalizedPassword = password != null ? password.toLowerCase().trim() : null;
        String normalizedName = name != null ? name.toLowerCase().trim() : null;
        String normalizedNickname = nickname != null ? nickname.toLowerCase().trim() : null;

        return new String[]{normalizedEmail, normalizedPassword, normalizedName, normalizedNickname};
    }

    // Login Request 전처리
    private String[] normalizeLoginData(String email, String password) {
        String normalizedEmail = email != null ? email.toLowerCase().trim() : null;
        String normalizedPassword = password != null ? password.toLowerCase().trim() : null;

        return new String[]{normalizedEmail, normalizedPassword};
    }

    // Signup Request 추가 검증 (DTO에서는 이메일 형식과 나머지 입력값의 길이 제한만 검증)
    // name : 한글 영문 공백만 사용
    // nickname : 예약어 제외, 한글 영문 숫자 언더스코어 사용
    private void validateSignupSecurity(String email, String password, String name, String nickname) {
        // 이름 패턴 검증
        if(!NAME_PATTERN.matcher(name).matches()) {
            throw new SecurityException("이름은 한글, 영문, 공백만 사용할 수 있습니다.");
        }

        // 닉네임 패턴 검증
        if(nickname != null && !NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new SecurityException("닉네임은 한글, 영문, 숫자, 언더스코어만 사용할 수 있습니다.");
        }

        // 닉네임 예약어 검증
        if(nickname != null && isReservedNickname(nickname)) {
            throw new SecurityException("사용할 수 없는 닉네임 입니다.");
        }
    }

    //========== 검증 메서드 ==========

    // 이메일 , 닉네임 중복 검사
    private void checkDuplicates(String email, String nickname) {
        // 이메일 중복 검사
        if(userRepository.existsByEmail(email)) {
            log.warn("이미 존재하는 이메일로 회원가입을 시도했습니다 : {}", email);
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        // 닉네임 중복 검사
        if(userRepository.existsByNickname(nickname)) {
            log.warn("이미 존재하는 닉네임으로 회원가입을 시도했습니다 : {}", nickname);
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
    }

    // 비밀번호 형식 검증 (DTO에서는 길이만 검증)
    // 영문 대소문자 숫자 특수문자 모두 포함.
    // 이외 요시 포함 금지
    private void validatePasswordSecurity(String password, String email, String name) {

        // 1. 영문 소문자 포함 검증
        if (!password.matches(".*[a-z].*")) {
            throw new SecurityException("비밀번호에 영문 소문자를 포함해야 합니다");
        }

        // 2. 영문 대문자 포함 검증
        if (!password.matches(".*[A-Z].*")) {
            throw new SecurityException("비밀번호에 영문 대문자를 포함해야 합니다");
        }

        // 3. 숫자 포함 검증
        if (!password.matches(".*[0-9].*")) {
            throw new SecurityException("비밀번호에 숫자를 포함해야 합니다");
        }

        // 4. 특수문자 포함 검증
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            throw new SecurityException("비밀번호에 특수문자를 포함해야 합니다");
        }

        // 5. 허용되지 않은 문자 검증 (영문, 숫자, 특수문자만 허용)
        if (!password.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*$")) {
            throw new SecurityException("비밀번호는 영문, 숫자, 특수문자만 사용할 수 있습니다");
        }
    }

    // 닉네임 예약어 검증
    private boolean isReservedNickname(String nickname) {
        if(nickname == null || nickname.trim().isEmpty()) {
            return false;
        }

        String normalizedNickname = nickname.toLowerCase().trim();

        return Arrays.stream(RESERVED_NICKNAMES).anyMatch(reserved -> normalizedNickname.contains(reserved.toLowerCase()));
    }
}