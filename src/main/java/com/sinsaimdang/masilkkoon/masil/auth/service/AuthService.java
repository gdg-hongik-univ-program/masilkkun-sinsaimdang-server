package com.sinsaimdang.masilkkoon.masil.auth.service;

import com.sinsaimdang.masilkkoon.masil.auth.entity.RefreshToken;
import com.sinsaimdang.masilkkoon.masil.auth.repository.RefreshTokenRepository;
import com.sinsaimdang.masilkkoon.masil.auth.util.JwtUtil;
import com.sinsaimdang.masilkkoon.masil.auth.validator.SignupValidator;
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
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SignupValidator signupValidator;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Value("${user.default-profile-image-url}")
    private String defaultProfileImageUrl;

    /**
     * 회원가입 메서드 <br>
     * 1. 입력값 전처리 (공백 제거, 이메일 소문자 처리) <br>
     * 2. 이메일, 닉네임 중복검사 <br>
     * 3. 비밀번호 암호화 <br>
     * 4. 사용자 생성 <br>
     * @param email
     * @param password
     * @param name
     * @param nickname
     * @return 생성된 사용자 Entity
     * @throws IllegalArgumentException : 중복된 이메일/닉네임 or 입력값이 유효하지 않은 경우
     * @throws SecurityException : 보안정책에 위반되는 입력값의 경우
     */
    @Transactional
    public User signup(String email, String password, String name, String nickname) {
        log.info("회원가입 시도 : 이메일 = {}", email);

        // 입력값 정규화
        NormalizedSignupData normalizedData = NormalizedSignupData.from(email, password, name, nickname);

        log.debug("정규화 된 회원가입 데이터 : {}", normalizedData);

        // 중복 검사
        checkDuplicates(normalizedData.getEmail(), normalizedData.getNickname());

        // 패턴 & 예약어 검사
        signupValidator.validateSignupData(
                normalizedData.getEmail(),
                normalizedData.getPassword(),
                normalizedData.getName(),
                normalizedData.getNickname()
        );

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 생성
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .nickname(nickname)
                .profileImageUrl(defaultProfileImageUrl)
                .build();

        User savedUser = userRepository.save(user);
        log.info("회원가입 완료 : ID : {} / Email : {}", savedUser.getId(), savedUser.getEmail());

        return savedUser;
    }

    /**
     * 로그인을 처리하고, JWT 토큰을 발급합니다<br>
     * 1. 입력값 정규화
     * 2. 사용자 조회
     * 3. 비밀번호 검증
     * 4. 토큰 발급 및 갱신
     * @param email
     * @param password
     * @return 액세스 토큰, 리프레쉬 토큰
     */
    @Transactional
    public Map<String, String> login(String email, String password) {
        log.info("로그인 시도 : 이메일 {}", email);

        NormalizedLoginData normalizedLoginData = NormalizedLoginData.from(email, password);

        log.debug("정규화된 로그인 데이터: {}", normalizedLoginData);

        // 사용자 조회
        User user = userRepository.findByEmail(normalizedLoginData.getEmail())
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 이메일로 로그인을 시도했습니다 : {}", normalizedLoginData.getEmail());
                    return new IllegalArgumentException("잘못된 이메일 또는 비밀번호 입니다");
                });

        // 비밀번호 검증
        if(! passwordEncoder.matches(normalizedLoginData.getPassword(), user.getPassword())) {
            log.warn("잘못된 비밀번호로 로그인을 시도했습니다 : {}", normalizedLoginData.getEmail());
            throw new IllegalArgumentException("잘못된 이메일 또는 비밀번호 입니다");
        }

        // 액세스 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                UserRole.USER.name()
        );

        // 리프레쉬 토큰 발급
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        // 기존 Refresh Token 무효화 (삭제)
        refreshTokenRepository.deleteByEmail(email);

        // 새로운 리프레쉬 토큰 서버에 저장
        saveRefreshToken(email, refreshToken);

        log.info("로그인 성공 : ID = {}, 이메일 = {}", user.getId(), user.getEmail());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    // 로그아웃
    @Transactional
    public void logout(String email) {
        log.info("로그아웃 시도 : 이메일 = {}", email);

        // 이메일 유효성 검증
        if (email == null || email.trim().isEmpty()) {
            log.warn("로그아웃 실패 - 이메일이 null이거나 비어있음");
            throw new IllegalArgumentException("이메일은 필수 항목입니다.");
        }

        String normalizedEmail = email.toLowerCase().trim();

        // 사용자 존재 여부 확인 (선택사항)
        if (!userRepository.existsByEmail(normalizedEmail)) {
            log.warn("로그아웃 실패 - 존재하지 않는 이메일: {}", normalizedEmail);
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // Refresh Token 삭제
        try {
            refreshTokenRepository.deleteByEmail(normalizedEmail);
            log.info("로그아웃 완료 : 이메일 = {}", normalizedEmail);
        } catch (Exception e) {
            log.error("로그아웃 중 토큰 삭제 실패 - 이메일: {}", normalizedEmail, e);
            throw new RuntimeException("로그아웃 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 액세스 토큰이 만료되었을 경우 리프레쉬 토큰을 통해 액세스 토큰을 갱신합니다
     * 1. 사용자의 리프레쉬 토큰과 서버에 저장된 리프레쉬 토큰을 대조합니다
     * 2. 리프레쉬 토큰 또한 만료되었을 경우 재로그인이 필요합니다.
     * 3. 사용자 정보를 조회하고 정보를 바탕으로 새로운 액세스 토큰을 발급합니다.
     * @param refreshToken
     * @return 액세스 토큰 & 리프레쉬 토큰
     */
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
            throw new IllegalArgumentException("만료된 리프레시 토큰입니다, 재로그인 필요");
        }

        // 사용자 조회
        User user = userRepository.findByEmail(tokenEntity.getEmail())
                .orElseThrow(() -> {
                    log.warn("토큰 갱신 실패 : 존재하지 않는 사용자, 이메일 = {}", tokenEntity.getEmail());
                    return new IllegalArgumentException("존재하지 않는 이메일입니다");
                });

        // 새로운 Access Token 생성
        String newAccessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                UserRole.USER.name());

        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        refreshTokenRepository.deleteByToken(refreshToken);
        saveRefreshToken(user.getEmail(), newRefreshToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        log.info("액세스 토큰 갱신 성공 : ID = {}, Email = {}", user.getId(), user.getEmail());
        return tokens;
    }

    /**
     * 이메일 중복 검사 API 전용 메서드
     * @param email
     * @return 해당 이메일이 이미 존재할 경우 True, 없을 경우 False
     */
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 닉네임 중복 검사 전용 API 전용 메서드
     * @param nickname
     * @return 해당 닉네임이 이미 존재할 경우 True, 없을 경우 False
     */
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
}