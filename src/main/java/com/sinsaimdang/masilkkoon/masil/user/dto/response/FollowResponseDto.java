package com.sinsaimdang.masilkkoon.masil.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 팔로우 요청에 대한 응답 DTO 클래스
 */
@Getter
@AllArgsConstructor
public class FollowResponseDto {
    private Long followId;
    private Long followerId;
    private Long followingId;
    private LocalDateTime createdAt;
}
