package com.sinsaimdang.masilkkoon.masil.auth.dto.response;

import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final String nickname;

    // User 엔티티를 받아서 DTO를 생성하는 정적 팩토리 메서드
    public static SignupResponse from(User user) {
        return new SignupResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname()
        );
    }
}