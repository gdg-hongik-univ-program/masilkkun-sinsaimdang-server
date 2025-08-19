package com.sinsaimdang.masilkkoon.masil.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "이메일은 필수 항목입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자리를 초과할 수 없습니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수 항목입니다")
    @Size(min = 8, max = 20, message = "비밀번호는 8-20자리 사이여야 합니다")
    private String password;

    @NotBlank(message = "이름은 필수 항목입니다")
    @Size(max = 10, message = "이름은 10자리를 초과할 수 없습니다")
    private String name;

    @NotBlank(message = "닉네임은 필수 항목입니다")
    @Size(max = 10, message = "닉네임은 10자리를 초과할 수 없습니다")
    private String nickname;
}
