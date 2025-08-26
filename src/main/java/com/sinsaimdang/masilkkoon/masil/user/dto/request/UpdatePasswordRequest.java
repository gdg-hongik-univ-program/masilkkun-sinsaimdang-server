package com.sinsaimdang.masilkkoon.masil.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 비밀번호 변경 요청을 위한 DTO 클래스
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdatePasswordRequest {
    @NotBlank(message = "새 비밀번호는 필수 항목입니다.")
    private String newPassword;
}
