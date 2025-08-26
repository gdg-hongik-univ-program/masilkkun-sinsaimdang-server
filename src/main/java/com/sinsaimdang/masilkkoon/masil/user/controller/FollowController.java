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

/**
 * 팔로우 관련 기능
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class
FollowController {

    private final FollowService followService;

    /**
     * 다른 사용자를 팔로우합니다.
     *
     * @param userId 팔로우할 사용자의 ID
     * @param currentUser 현재 사용자 정보
     * @return 팔로우 정보를 담은 DTO
     */
    @PostMapping("/{userId}/follow")
    public ResponseEntity<Map<String, Object>> followUser(
            @PathVariable Long userId, CurrentUser currentUser) {

        log.info("API REQ : POST api/user/{}/follow | 요청자 ID {}", userId, currentUser.getId());

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

        log.info("API RES : POST api/user/{}/follow | 요청자 ID {}", userId, currentUser.getId());
        return ApiResponseUtil.success("팔로우가 완료되었습니다", responseDto);
    }

    /**
     * 다른 사용자를 언팔로우합니다.
     *
     * @param userId 언팔로우할 사용자의 ID
     * @param currentUser 현재 사용자 정보
     * @return 언팔로우 정보를 담은 DTO
     */
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<Map<String, Object>> unfollowUser(
            @PathVariable Long userId, CurrentUser currentUser) {

        log.info("API REQ : DELETE api/user/{}/follow | 요청자 ID {}", userId, currentUser.getId());

        if(!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("로그인이 필요합니다");
        }

        followService.unfollowUser(currentUser.getId(), userId);

        UnfollowResponseDto responseDto = new UnfollowResponseDto(
                currentUser.getId(),
                userId
        );

        log.info("API RES : DELETE api/user/{}/follow | 요청자 ID {}", userId, currentUser.getId());
        return ApiResponseUtil.success("언팔로우가 완료되었습니다", responseDto);
    }

    /**
     * 특정 사용자에 대한 팔로우 상태를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @param currentUser 현재 사용자 정보
     * @return 팔로우 상태 정보를 담은 DTO
     */
    @GetMapping("/{userId}/follow-status")
    public ResponseEntity<Map<String, Object>> getFollowStatus(
            @PathVariable Long userId, CurrentUser currentUser) {

        log.info("API REQ : GET api/user/{}/follow-status | 요청자 ID {}", userId, currentUser.getId());

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

        log.info("API RES : GET api/user/{}/follow-status | 요청자 ID {}", userId, currentUser.getId());
        return ApiResponseUtil.success("팔로우 상태 조회 성공", responseDto);
    }

    /**
     * 특정 사용자의 팔로워 및 팔로잉 수를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 팔로우 정보를 담은 DTO
     */
    @GetMapping("/{userId}/follow-info")
    public ResponseEntity<Map<String, Object>> getFollowInfo(@PathVariable Long userId) {
        log.info("API REQ : GET api/user/{}/follow-info", userId);

        UserDto responseDto = followService.getFollowStatus(userId);

        log.info("API RES : GET api/user/{}/follow-info", userId);
        return ApiResponseUtil.success("팔로우 통계 조회 성공", responseDto);
    }

    /**
     * 특정 사용자의 팔로워 목록을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @param pageable 페이징 정보
     * @return 팔로워 목록을 담은 DTO의 Slice
     */
    @GetMapping("/{userId}/followers")
    public ResponseEntity<Map<String, Object>> getFollowers(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("API REQ : GET api/user/{}/followers", userId);

        Slice<User> followers = followService.getFollowers(userId, pageable);
        Slice<UserDto> followerDtos = followers.map(UserDto::from);

        log.info("API RES : GET api/user/{}/followers", userId);
        return ApiResponseUtil.success("팔로워 목록 조회 성공", followerDtos);
    }

    /**
     * 특정 사용자의 팔로잉 목록을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @param pageable 페이징 정보
     * @return 팔로잉 목록을 담은 DTO의 Slice
     */
    @GetMapping("/{userId}/followings")
    public ResponseEntity<Map<String, Object>> getFollowings(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("API REQ : GET api/user/{}/followings", userId);
        Slice<User> followings = followService.getFollowing(userId, pageable);
        Slice<UserDto> followingDtos = followings.map(UserDto::from);

        log.info("API RES : GET api/user/{}/followings", userId);
        return ApiResponseUtil.success("팔로잉 목록 조회 성공", followingDtos);
    }
}