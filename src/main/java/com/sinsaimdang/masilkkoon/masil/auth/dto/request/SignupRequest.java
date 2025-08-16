package com.sinsaimdang.masilkkoon.masil.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * API Endpoint : POST /api/auth/signup <br><br>
 *   request template: <br>
 *   { <br>
 *       "email" : "user@email.com", <br>
 *       "password" : "Password123!", <br>
 *       "name" : "마실꾼", <br>
 *       "nickname" : "신사임당" <br>
 *   } <br><br>
 * 회원가입 요청을 담는 DTO<br>
 * <br>
 * 책임 : SignupRequest 에 대한 입력값 검증<br>
 *<br>
 * @see com.sinsaimdang.masilkkoon.masil.user.entity.User
 * @see com.sinsaimdang.masilkkoon.masil.auth.service.AuthService#signup(String, String, String, String)
 * @see com.sinsaimdang.masilkkoon.masil.auth.controller.AuthController#signup(SignupRequest)
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    /**
     * 사용자 이메일 주소 -> 아이디로 사용<br>
     * <br>
     * - @NotBlank : null 또는 blank 시 실패<br>
     * - @Email : email 형식 시 실패<br>
     * - @Size : 길이 10 초과 시 실패<br>
     * - 이메일 중복 여부는 Auth Service 에서 검증<br>
     * @see com.sinsaimdang.masilkkoon.masil.auth.service.AuthService
     */
    @NotBlank(message = "이메일은 필수 항목입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자리를 초과할 수 없습니다")
    private String email;

    /**
     * 비밀번호<br>
     * <br>
     * -@NotBlank : null 또는 blank 시 실패<br>
     * -@Size : 8~20 자리로 제한<br>
     * -암호화는 SpringSecurity PasswordEncoder 사용<br>
     * @see com.sinsaimdang.masilkkoon.masil.auth.service.AuthService
     */

    @NotBlank(message = "비밀번호는 필수 항목입니다")
    @Size(min = 8, max = 20, message = "비밀번호는 8-20자리 사이여야 합니다")
    private String password;

    /**
     * 사용자 이름<br>
     * <br>
     * -@NotBlank : null 또는 blank 시 실패<br>
     * -@Size : 10자리 초과 시 실패<br>
     */

    @NotBlank(message = "이름은 필수 항목입니다")
    @Size(max = 10, message = "이름은 10자리를 초과할 수 없습니다")
    private String name;

    /**
     * 사용자 닉네임<br>
     * <br>
     * -@NotBlank : null 또는 blank 시 실패<br>
     * -@Size : 10자리 초과 시 실패<br>
     */

    @NotBlank(message = "닉네임은 필수 항목입니다")
    @Size(max = 10, message = "닉네임은 10자리를 초과할 수 없습니다")
    private String nickname;
}
