package com.sinsaimdang.masilkkoon.masil.auth.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NormalizedSignupData {
    private final String email;
    private final String password;
    private final String name;
    private final String nickname;

    /**
     * 정규환된 회원가입 데이터 객체를 생성
     * @param rawEmail
     * @param rawPassword
     * @param rawName
     * @param rawNickname
     * @return 정규화된 회원가입 데이터
     */
    public static NormalizedSignupData from(String rawEmail, String rawPassword, String rawName, String rawNickname) {
        String normalizedEmail = rawEmail != null ? rawEmail.toLowerCase().trim() : null;
        String normalizedPassword = rawPassword != null ?rawPassword.trim() : null;
        String normalizedName = rawName != null ? rawName.trim() : null;
        String normalizedNickname = rawNickname != null ? rawNickname.trim() : null;

        return new NormalizedSignupData(normalizedEmail, normalizedPassword, normalizedName, normalizedNickname);
    }

    /**
     * 디버깅 목적으로 정규화 된 회원가입 데이터를 반환함 <br>
     * 보안상 목적으로 비밀번호는 마스킹하여 표시합니다
     * @return 정규화된 회원가입 데이터
     */
    @Override
    public String toString() {
        return "NormalizedSignupData{" +
                "email='" + email + '\'' +
                ", password='***'" + // 보안상 마스킹
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
