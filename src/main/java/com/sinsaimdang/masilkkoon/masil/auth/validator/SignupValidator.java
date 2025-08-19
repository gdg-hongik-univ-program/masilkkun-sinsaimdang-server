package com.sinsaimdang.masilkkoon.masil.auth.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 회원가입 입력값 검증을 담당하는 클래스<br>
 * 1. 이름 형식 검증 : 한글, 영문만 허용<br>
 * 2. 닉네임 형식 검증 : 한글, 영문, 숫자, 언더스코어만 허용 & 예약어 사용 금지<br>
 * 3. 비밀번호 형식 검증<br>
 */
@Component
@Slf4j
public class SignupValidator {

    // 예약어 목록
    private static final String[] RESERVED_NICKNAMES = {
            "admin", "administrator", "관리자", "운영자", "system", "root",
            "user", "guest", "anonymous", "null", "undefined", "test",
            "support", "help", "info", "service", "official", "마실꾼", "신사임당"
    };

    // 이름 패턴 (한글, 영어, 공백만을 허용)
    private static final Pattern NAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z]+$");

    // 닉네임 패턴 (한글, 영어, 숫자, 언더스코어만을 허용)
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9_]+$");

    public void validateSignupData(String email, String password, String name, String nickname) {
        log.debug("회원가입 데이터 검증 시작 - 이메일: {}, 이름: {}, 닉네임: {}", email, name, nickname);

        // 1. 이름 형식 검증
        validateName(name);

        // 2. 닉네임 형식 및 예약어 검증
        validateNickname(nickname);

        // 3. 비밀번호 보안 정책 검증
        validatePassword(password);

        log.debug("회원가입 데이터 검증 완료");
    }

    public void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new SecurityException("이름은 필수 항목입니다.");
        }

        if (!NAME_PATTERN.matcher(name).matches()) {
            log.warn("잘못된 이름 형식: {}", name);
            throw new SecurityException("이름은 한글, 영문만 사용할 수 있습니다.");
        }

        log.debug("이름 형식 검증 통과: {}", name);
    }

    public void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new SecurityException("닉네임은 필수 항목입니다.");
        }

        if (nickname.length() > 10) {
            throw new SecurityException("닉네임은 10자리를 초과할 수 없습니다.");
        }

        // 패턴 검증
        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            log.warn("잘못된 닉네임 형식: {}", nickname);
            throw new SecurityException("닉네임은 한글, 영문, 숫자, 언더스코어만 사용할 수 있습니다.");
        }

        // 예약어 검증
        if (isReservedNickname(nickname)) {
            log.warn("예약어 닉네임 사용 시도: {}", nickname);
            throw new SecurityException("예약어는 닉네임으로 사용할 수 없습니다.");
        }

        log.debug("닉네임 검증 통과: {}", nickname);
    }

    public void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new SecurityException("비밀번호는 필수 항목입니다.");
        }

        if (password.length() < 8 || password.length() > 20) {
            throw new SecurityException("비밀번호는 8-20자리 사이여야 합니다.");
        }

        // 1. 영문 소문자 포함 검증
        if (!password.matches(".*[a-z].*")) {
            throw new SecurityException("비밀번호에 영문 소문자를 포함해야 합니다.");
        }

        // 2. 영문 대문자 포함 검증
        if (!password.matches(".*[A-Z].*")) {
            throw new SecurityException("비밀번호에 영문 대문자를 포함해야 합니다.");
        }

        // 3. 숫자 포함 검증
        if (!password.matches(".*[0-9].*")) {
            throw new SecurityException("비밀번호에 숫자를 포함해야 합니다.");
        }

        // 4. 특수문자 포함 검증
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            throw new SecurityException("비밀번호에 특수문자를 포함해야 합니다.");
        }

        // 5. 허용되지 않은 문자 검증 (영문, 숫자, 특수문자만 허용)
        if (!password.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*$")) {
            throw new SecurityException("비밀번호는 영문, 숫자, 특수문자만 사용할 수 있습니다.");
        }

        log.debug("비밀번호 보안 정책 검증 통과");
    }

    private boolean isReservedNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return false;
        }

        String normalizedNickname = nickname.toLowerCase().trim();

        return Arrays.stream(RESERVED_NICKNAMES)
                .anyMatch(reserved -> normalizedNickname.contains(reserved.toLowerCase()));
    }
}
