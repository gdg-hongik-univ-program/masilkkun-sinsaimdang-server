package com.sinsaimdang.masilkkoon.masil.auth.service;

import com.sinsaimdang.masilkkoon.masil.auth.repository.RefreshTokenRepository;
import com.sinsaimdang.masilkkoon.masil.auth.util.JwtUtil;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * @param email 이메일
     * @param password 평문 비밀번호
     * @param name 이름
     * @param nickname 사용자 닉네임
     * @return 생성된 사용자 정보
     */
    @Transactional
    public User signup(String email, String password, String name, String nickname) {
        log.info("회원가입 시도 : 이메일 = {}", email);
    }
}
