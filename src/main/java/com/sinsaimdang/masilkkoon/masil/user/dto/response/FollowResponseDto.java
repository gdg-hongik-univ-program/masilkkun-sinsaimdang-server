package com.sinsaimdang.masilkkoon.masil.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FollowResponseDto {
    private Long followId;
    private Long followerId;
    private Long followingId;
    private LocalDateTime createdAt;
}
