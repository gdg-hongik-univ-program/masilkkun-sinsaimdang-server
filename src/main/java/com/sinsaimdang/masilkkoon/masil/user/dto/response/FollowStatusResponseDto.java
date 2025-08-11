package com.sinsaimdang.masilkkoon.masil.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowStatusResponseDto {
    private Long currentUserId;
    private Long targetUserId;
    private boolean isFollowing;
    private boolean isMutualFollow;
}
