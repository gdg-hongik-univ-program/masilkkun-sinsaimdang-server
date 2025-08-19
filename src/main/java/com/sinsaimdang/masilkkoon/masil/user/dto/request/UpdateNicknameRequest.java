package com.sinsaimdang.masilkkoon.masil.user.dto.request;

import jakarta.validation.constraints.NotBlank;
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
    private String nickname;
}
