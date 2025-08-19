package com.sinsaimdang.masilkkoon.masil.user.controller;

import com.sinsaimdang.masilkkoon.masil.auth.dto.CurrentUser;
import com.sinsaimdang.masilkkoon.masil.common.util.ApiResponseUtil;
import com.sinsaimdang.masilkkoon.masil.user.dto.*;
import com.sinsaimdang.masilkkoon.masil.user.dto.response.FollowResponseDto;
import com.sinsaimdang.masilkkoon.masil.user.dto.response.FollowStatusResponseDto;
import com.sinsaimdang.masilkkoon.masil.user.dto.response.UnfollowResponseDto;
import com.sinsaimdang.masilkkoon.masil.user.entity.Follow;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class
FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}/follow")
    public ResponseEntity<Map<String, Object>> followUser(
            @PathVariable Long userId, CurrentUser currentUser) {

        log.info("API REQ : POST api/users/{}/follow | 요청자 ID {}", userId, currentUser.getId());

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("로그인이 필요합니다.");
        }

        Follow follow = followService.followUser(currentUser.getId(), userId);

        FollowResponseDto responseDto = new FollowResponseDto(
                follow.getId(),
                currentUser.getId(),
                userId,
                follow.getCreatedAt()
        );

        log.info("API RES : POST api/users/{}/follow | 요청자 ID {}", userId, currentUser.getId());
        return ApiResponseUtil.success("팔로우가 완료되었습니다", responseDto);
    }

    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<Map<String, Object>> unfollowUser(
            @PathVariable Long userId, CurrentUser currentUser) {

        log.info("API REQ : DELETE api/users/{}/follow | 요청자 ID {}", userId, currentUser.getId());

        if(!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("로그인이 필요합니다");
        }

        followService.unfollowUser(currentUser.getId(), userId);

        UnfollowResponseDto responseDto = new UnfollowResponseDto(
                currentUser.getId(),
                userId
        );

        log.info("API RES : DELETE api/users/{}/follow | 요청자 ID {}", userId, currentUser.getId());
        return ApiResponseUtil.success("언팔로우가 완료되었습니다", responseDto);
    }

    @GetMapping("/{userId}/follow-status")
    public ResponseEntity<Map<String, Object>> getFollowStatus(
            @PathVariable Long userId, CurrentUser currentUser) {

        log.info("API REQ : GET api/users/{}/follow-status | 요청자 ID {}", userId, currentUser.getId());

        if(!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("로그인이 필요합니다");
        }

        boolean isFollowing = followService.isFollowing(currentUser.getId(), userId);
        boolean isMutualFollow = followService.isMutualFollow(currentUser.getId(), userId);

        FollowStatusResponseDto responseDto = new FollowStatusResponseDto(
                currentUser.getId(),
                userId,
                isFollowing,
                isMutualFollow
        );

        log.info("API RES : GET api/users/{}/follow-status | 요청자 ID {}", userId, currentUser.getId());
        return ApiResponseUtil.success("팔로우 상태 조회 성공", responseDto);
    }

    @GetMapping("/{userId}/follow-info")
    public ResponseEntity<Map<String, Object>> getFollowInfo(@PathVariable Long userId) {
        log.info("API REQ : GET api/users/{}/follow-info", userId);

        UserDto responseDto = followService.getFollowStatus(userId);

        log.info("API RES : GET api/users/{}/follow-info", userId);
        return ApiResponseUtil.success("팔로우 통계 조회 성공", responseDto);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<Map<String, Object>> getFollowers(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("API REQ : GET api/users/{}/followers", userId);

        Slice<User> followers = followService.getFollowers(userId, pageable);
        Slice<UserDto> followerDtos = followers.map(UserDto::from);

        log.info("API RES : GET api/users/{}/followers", userId);
        return ApiResponseUtil.success("팔로워 목록 조회 성공", followerDtos);
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<Map<String, Object>> getFollowings(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("API REQ : GET api/users/{}/followings", userId);
        Slice<User> followings = followService.getFollowing(userId, pageable);
        Slice<UserDto> followingDtos = followings.map(UserDto::from);

        log.info("API RES : GET api/users/{}/followings", userId);
        return ApiResponseUtil.success("팔로잉 목록 조회 성공", followingDtos);
    }
}