package com.sinsaimdang.masilkkoon.masil.user.controller;

import com.sinsaimdang.masilkkoon.masil.auth.dto.CurrentUser;
import com.sinsaimdang.masilkkoon.masil.common.util.ApiResponseUtil;
import com.sinsaimdang.masilkkoon.masil.user.dto.FollowStatusDto;
import com.sinsaimdang.masilkkoon.masil.user.dto.UserDto;
import com.sinsaimdang.masilkkoon.masil.user.entity.Follow;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}/follow")
    public ResponseEntity<Map<String, Object>> followUser(
            @PathVariable Long userId, CurrentUser currentUser) {

        log.info("팔로우 요청 - 팔로워 {} -> 팔로잉 {}", currentUser.getId(), userId);

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("로그인이 필요합니다.");
        }

        Follow follow = followService.followUser(currentUser.getId(), userId);
        log.info("팔로우 성공 - 팔로워 :{} -> 팔로잉 {}", currentUser.getId(), userId);

        return ApiResponseUtil.success("팔로우가 완료되었습니다",
                Map.of(
                        "followId", follow.getId(),
                        "followerId", currentUser.getId(),
                        "followingId", userId,
                        "createdAt", follow.getCreatedAt()
                ));
    }

    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<Map<String, Object>> unfollowUser(
            @PathVariable Long userId, CurrentUser currentUser) {

        log.info("언팔로우 요청 - 팔로워 {} -> 팔로잉 {}", currentUser.getId(), userId);

        if(!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("로그인이 필요합니다");
        }

        followService.unfollowUser(currentUser.getId(), userId);
        log.info("언팔로우 성공 - 팔로워 {} -> 팔로잉 {}", currentUser.getId(), userId);

        return ApiResponseUtil.success(
                "언팔로우가 완료되었습니다",
                Map.of(
                        "followerId", currentUser.getId(),
                        "followingId", userId
                ));
    }

    @GetMapping("/{userId}/follow-status")
    public ResponseEntity<Map<String, Object>> getFollowStatus(
            @PathVariable Long userId, CurrentUser currentUser) {

        log.debug("팔로우 상태 확인 - 현재 사용자 {}, 대상 사용자 {}", currentUser.getId(), userId);

        if(!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("로그인이 필요합니다");
        }

        boolean isFollowing = followService.isFollowing(currentUser.getId(), userId);
        boolean isMutualFollow = followService.isMutualFollow(currentUser.getId(), userId);

        return ApiResponseUtil.success(
                "팔로우 상태 조회 성공",
                Map.of(
                        "currentUserId", currentUser.getId(),
                        "targetUserId", userId,
                        "isFollowing", isFollowing,
                        "isMutualFollow", isMutualFollow
                ));
    }

    @GetMapping("/{userId}/follow-info")
    public ResponseEntity<Map<String, Object>> getFollowInfo(@PathVariable Long userId) {
        log.debug("팔로우 통계 정보 조회 - 사용자 {}", userId);
        FollowStatusDto followStatus = followService.getFollowStatus(userId);
        return ApiResponseUtil.success("팔로우 통계 조회 성공", followStatus);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<Map<String, Object>> getFollowers(@PathVariable Long userId, Pageable pageable) {
        log.debug("팔로워 목록 조회 요청 - 사용자 {}, 페이지 {}", userId, pageable);
        Page<User> followers = followService.getFollowers(userId, pageable);
        Page<UserDto> followerDtos = followers.map(UserDto::from);
        return ApiResponseUtil.success("팔로워 목록 조회 성공", followerDtos);
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<Map<String, Object>> getFollowings(@PathVariable Long userId, Pageable pageable) {
        log.debug("팔로잉 목록 조회 요청 - 사용자 {}, 페이지 {}", userId, pageable);
        Page<User> followings = followService.getFollowing(userId, pageable);
        Page<UserDto> followingDtos = followings.map(UserDto::from);
        return ApiResponseUtil.success("팔로잉 목록 조회 성공", followingDtos);
    }
}