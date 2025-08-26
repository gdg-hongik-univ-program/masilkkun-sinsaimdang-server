package com.sinsaimdang.masilkkoon.masil.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 언팔로우 요청에 대한 응답 DTO 클래스
 */
@Getter
@AllArgsConstructor
public class UnfollowResponseDto {
    private Long followerId;
    private Long followingId;
}
