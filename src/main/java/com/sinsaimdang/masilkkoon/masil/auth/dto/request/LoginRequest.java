package com.sinsaimdang.masilkkoon.masil.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * API Endpoint : POST /api/auth/login <br><br>
 *   request template: <br>
 *   { <br>
 *       "email" : "user@email.com", <br>
 *       "password" : "Password123!", <br>
 *   } <br><br>
 * 로그인 요청을 담는 DTO<br>
 * <br>
 * 책임 : Login Request 에 대한 입력값 검증<br>
 *<br>
 * @see com.sinsaimdang.masilkkoon.masil.user.entity.User
 * @see com.sinsaimdang.masilkkoon.masil.auth.service.AuthService#login(String, String)
 * @see com.sinsaimdang.masilkkoon.masil.auth.controller.AuthController#login(LoginRequest)
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * 사용자 이메일 주소 -> 아이디로 사용<br>
     * <br>
     * - @NotBlank : null 또는 blank 시 실패<br>
     * - @Email : email 형식 시 실패<br>
     * - @Size : 길이 10 초과 시 실패<br>
     * - 이메일 중복 여부는 Auth Service 에서 검증<br>
     * @see com.sinsaimdang.masilkkoon.masil.auth.service.AuthService
     */
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    /**
     * 비밀번호<br>
     * <br>
     * -@NotBlank : null 또는 blank 시 실패<br>
     * -@Size : 8~20 자리로 제한<br>
     * -암호 검증은 PasswordEncoder.matches() 사용
     * @see com.sinsaimdang.masilkkoon.masil.auth.service.AuthService
     */

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}
