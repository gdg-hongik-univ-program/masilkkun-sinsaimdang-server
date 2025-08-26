package com.sinsaimdang.masilkkoon.masil.user.entity;

import lombok.Getter;

/**
 * 사용자의 역할을 정의하는 열거형 클래스입니다.
 */
@Getter
public enum UserRole {
    /**
     * 일반 사용자
     */
    USER("일반 사용자"),
    /**
     * 관리자
     */
    ADMIN("관리자");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }


}
