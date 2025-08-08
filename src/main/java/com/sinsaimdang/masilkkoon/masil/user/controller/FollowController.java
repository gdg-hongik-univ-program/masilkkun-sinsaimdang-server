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

        try {
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
        } catch (IllegalArgumentException e) {
            log.warn("팔로우 실패 - 팔로워 {} -> 팔로잉 {} - 사유 {}", currentUser.getId(), userId, e.getMessage());
            return ApiResponseUtil.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("팔로우 요청 처리 중 서버 오류", e);
            return ApiResponseUtil.internalServerError("서버 내부 오류 발생");
        }
    }

    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<Map<String, Object>> unfollowUser(
            @PathVariable Long userId, CurrentUser currentUser) {

        log.info("언팔로우 요청 - 팔로워 {} -> 팔로잉 {}", currentUser.getId(), userId);

        try {
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
        } catch (IllegalArgumentException e) {
            log.warn("언팔로우 실패 - 팔로워 {} -> 팔로잉 {}, 사유 {}", currentUser.getId(), userId, e.getMessage());
            return ApiResponseUtil.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("언팔로우 요청 처리 중 서버 오류", e);
            return ApiResponseUtil.internalServerError("서버 내부 오류 발생");
        }
    }

    @GetMapping("/{userId}/follow-status")
    public ResponseEntity<Map<String, Object>> getFollowStatus(
            @PathVariable Long userId, CurrentUser currentUser) {

        log.debug("팔로우 상태 확인 - 현재 사용자 {}, 대상 사용자 {}", currentUser.getId(), userId);

        try {
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
        } catch (Exception e) {
            log.error("팔로우 상태 확인 중 서버 오류 : 현재 사용자 {} -> 대상 사용자 {}, 사유 {}", currentUser.getId(), userId, e.getMessage());
            return ApiResponseUtil.internalServerError("서버 내부 오류 발생");
        }
    }

    @GetMapping("/{userId}/follow-info")
    public ResponseEntity<Map<String, Object>> getFollowInfo(@PathVariable Long userId) {
        log.debug("팔로우 통계 정보 조회 - 사용자 {}", userId);

        try {
            FollowStatusDto followStatus = followService.getFollowStatus(userId);

            return ApiResponseUtil.success(
                    "팔로우 통계 조회 성공",
                    followStatus
            );
        } catch (IllegalArgumentException e) {
            log.warn("팔로우 통계 조회 실패 - 사용자 {}, 사유 {}", userId, e.getMessage());
            return ApiResponseUtil.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("팔로우 통계 확인 중 서버 오류 : 사용자 {}, 사유 {}", userId, e.getMessage());
            return ApiResponseUtil.internalServerError("서버 내부 오류 발생");
        }
    }

    @GetMapping("/{userId}/follwers")
    public ResponseEntity<Map<String, Object>> getFollowers(@PathVariable Long userId, Pageable pageable) {

        log.debug("팔로워 목록 조회 요청 - 사용자 {}, 페이지 {}", userId, pageable);

        try {
            Page<User> followers = followService.getFollowers(userId, pageable);

            Page<UserDto> followerDtos = followers.map(UserDto::from);

            return ApiResponseUtil.success(
                    "팔로워 목록 조회 성공",
                    followerDtos
            );
        } catch (IllegalArgumentException e) {
            log.warn("팔로워 목록 조회 실패 - 사용자 {}, 사유 {}", userId, e.getMessage());
            return ApiResponseUtil.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponseUtil.internalServerError("서버 내부 오류 발생");
        }
    }

    @GetMapping("/{userId}/follwings")
    public ResponseEntity<Map<String, Object>> getFollowings(@PathVariable Long userId, Pageable pageable) {

        log.debug("팔로잉 목록 조회 요청 - 사용자 {}, 페이지 {}", userId, pageable);

        try {
            Page<User> followings = followService.getFollowing(userId, pageable);

            Page<UserDto> followingDtos = followings.map(UserDto::from);

            return ApiResponseUtil.success(
                    "팔로워 목록 조회 성공",
                    followingDtos
            );
        } catch (IllegalArgumentException e) {
            log.warn("팔로잉 목록 조회 실패 - 사용자 {}, 사유 {}", userId, e.getMessage());
            return ApiResponseUtil.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponseUtil.internalServerError("서버 내부 오류 발생");
        }
    }
}
