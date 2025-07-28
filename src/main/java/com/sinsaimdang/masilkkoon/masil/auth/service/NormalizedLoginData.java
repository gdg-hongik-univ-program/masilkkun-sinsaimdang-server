package com.sinsaimdang.masilkkoon.masil.auth.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NormalizedLoginData {
    private final String email;
    private final String password;

    /**
     * 정규화된 로그인 데이터
     */
    public static NormalizedLoginData from(String rawEmail, String rawPassword) {
        String normalizedEmail = rawEmail != null ? rawEmail.toLowerCase().trim() : null;
        String normalizedPassword = rawPassword != null ? rawPassword.trim() : null;

        return new NormalizedLoginData(normalizedEmail, normalizedPassword);
    }

    @Override
    public String toString() {
        return "NormalizedLoginData{" +
                "email='" + email + '\'' +
                ", password='***'" + // 보안상 마스킹
                '}';
    }
}