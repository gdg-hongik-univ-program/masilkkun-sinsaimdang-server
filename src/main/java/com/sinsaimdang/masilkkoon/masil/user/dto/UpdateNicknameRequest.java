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
public class UpdateNicknameRequest {
    @NotBlank(message = "닉네임은 필수 항목입니다")
    @Size(max = 10, message = "닉네임은 10자리를 초과할 수 없습니다")
    private String nickname;
}
