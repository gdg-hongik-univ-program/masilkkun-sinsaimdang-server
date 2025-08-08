package com.sinsaimdang.masilkkoon.masil.user.dto;

import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowStatusDto {

    private Long userId;
    private int follwerCount;
    private int followingCount;

    public static FollowStatusDto from(User user) {
        return FollowStatusDto.builder()
                .userId(user.getId())
                .follwerCount(user.getFollowerCount())
                .followingCount(user.getFollowingCount())
                .build();
    }
}
