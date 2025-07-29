package com.sinsaimdang.masilkkoon.masil.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdatePasswordRequest {
    @NotBlank(message = "새 비밀번호는 필수 항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자리 사이여야 합니다.")
    private String newPassword;
}
