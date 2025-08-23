//package com.sinsaimdang.masilkkoon.masil.auth.service;
//
//import com.sinsaimdang.masilkkoon.masil.auth.util.JwtUtil;
//import com.sinsaimdang.masilkkoon.masil.user.entity.User;
//import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//@Transactional // 각 테스트 후 DB 롤백
//class AuthServiceTest {
//
//    @Autowired
//    private AuthService authService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Test
//    @DisplayName("회원가입 성공")
//    void signup_Success() {
//        // given
//        String email = "newuser@example.com";
//        String password = "Password123!";
//        String name = "새사용자";
//        String nickname = "뉴비";
//
//        // when
//        User signedUpUser = authService.signup(email, password, name, nickname);
//
//        // then
//        assertThat(signedUpUser).isNotNull();
//        assertThat(signedUpUser.getEmail()).isEqualTo(email);
//
//        // DB에 저장된 비밀번호가 암호화되었는지 확인
//        User foundUser = userRepository.findByEmail(email).get();
//        assertTrue(passwordEncoder.matches(password, foundUser.getPassword()));
//    }
//
//    @Test
//    @DisplayName("회원가입 실패 - 이메일 중복")
//    void signup_Fail_DuplicateEmail() {
//        // given
//        // 이미 존재하는 사용자 생성
//        authService.signup("duplicate@example.com", "Password123!", "기존유저", "기존닉");
//
//        // when & then
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            authService.signup("duplicate@example.com", "Password456!", "새유저", "새닉");
//        });
//        assertThat(exception.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");
//    }
//
//    @Test
//    @DisplayName("로그인 성공 및 토큰 발급")
//    void login_Success() {
//        // given
//        String email = "loginuser@example.com";
//        String password = "Password123!";
//        authService.signup(email, password, "로그인유저", "로그인닉");
//
//        // when
//        Map<String, String> tokens = authService.login(email, password);
//
//        // then
//        assertThat(tokens).containsKey("accessToken");
//        assertThat(tokens).containsKey("refreshToken");
//
//        String accessToken = tokens.get("accessToken");
//        String userEmailFromToken = jwtUtil.extractUserEmail(accessToken);
//        assertThat(userEmailFromToken).isEqualTo(email);
//    }
//
//    @Test
//    @DisplayName("로그인 실패 - 잘못된 비밀번호")
//    void login_Fail_WrongPassword() {
//        // given
//        String email = "loginuser@example.com";
//        authService.signup(email, "Password123!", "로그인유저", "로그인닉");
//
//        // when & then
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            authService.login(email, "wrong-password");
//        });
//        assertThat(exception.getMessage()).isEqualTo("잘못된 이메일 또는 비밀번호 입니다");
//    }
//}