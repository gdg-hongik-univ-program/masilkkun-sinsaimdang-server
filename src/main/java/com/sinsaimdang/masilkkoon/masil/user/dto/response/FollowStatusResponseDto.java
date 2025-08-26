package com.sinsaimdang.masilkkoon.masil.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 팔로우 상태 조회에 대한 응답 DTO 클래스
 */
@Getter
@AllArgsConstructor
public class FollowStatusResponseDto {
    private Long currentUserId;
    private Long targetUserId;
    private boolean isFollowing;
    private boolean isMutualFollow;
}
