package com.sinsaimdang.masilkkoon.masil.user.dto;

import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보를 전달하기 DTO 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String profileImageUrl;
    private int followerCount;
    private int followingCount;

    /**
     * User 엔티티를 UserDto로 변환합니다.
     *
     * @param user 변환할 User 엔티티
     * @return 변환된 UserDto 객체
     */
    public static UserDto from(User user){
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .followerCount(user.getFollowerCount())
                .followingCount(user.getFollowingCount())
                .build();
    }
}
