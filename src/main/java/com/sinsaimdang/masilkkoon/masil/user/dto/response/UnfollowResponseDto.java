package com.sinsaimdang.masilkkoon.masil.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UnfollowResponseDto {
    private Long followerId;
    private Long followingId;
}
