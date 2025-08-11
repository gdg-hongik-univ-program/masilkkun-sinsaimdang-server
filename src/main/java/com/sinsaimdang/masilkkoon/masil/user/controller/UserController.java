package com.sinsaimdang.masilkkoon.masil.user.controller;

import com.sinsaimdang.masilkkoon.masil.auth.dto.CurrentUser;
import com.sinsaimdang.masilkkoon.masil.common.util.ApiResponseUtil;
import com.sinsaimdang.masilkkoon.masil.user.dto.UpdateNicknameRequest;
import com.sinsaimdang.masilkkoon.masil.user.dto.UpdatePasswordRequest;
import com.sinsaimdang.masilkkoon.masil.user.dto.UpdateProfileImageRequest;
import com.sinsaimdang.masilkkoon.masil.user.dto.UserDto;
import com.sinsaimdang.masilkkoon.masil.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * User 관련 API 엔드포인트
 * 1. 현재 사용자 정보 조회 : GET /api/user/me
 * 2. 사용자 프로필 조회 : GET /api/user/profile
 * 3. 닉네임 변경 : PATCH GET /api/user/nickname
 * 4. 비밀번호 변경 : PATCH /api/user/password
 * 5. 회원 탈퇴 : DELETE /api/user/me
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(CurrentUser currentUser) {
        log.info("현재 사용자 정보 조회 요청");

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("인증되지 않은 사용자 입니다");
        }

        log.info("사용자 정보 조회 성공 - ID = {}, 이메일 = {}", currentUser.getId(), currentUser.getEmail());

        return ApiResponseUtil.success("사용자 정보 조회 성공", currentUser.toMap());
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(CurrentUser currentUser) {
        log.info("사용자 프로필 조회 요청");

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("인증되지 않은 사용자입니다.");
        }

        UserDto userProfile = userService.findById(currentUser.getId())
                .orElseThrow(() -> {
                    log.warn("프로필 조회 실패 - 존재하지 않는 사용자 ID {}", currentUser.getId());
                    return new IllegalArgumentException("사용자 정보를 찾을 수 없습니다");
                });

        log.info("프로필 조회 성공 - ID {}, 이메일 {}", userProfile.getId(), userProfile.getEmail());

        return ApiResponseUtil.success("프로필 조회 성공", Map.of(
                "id", userProfile.getId(),
                "email", userProfile.getEmail(),
                "name", userProfile.getName(),
                "nickname", userProfile.getNickname(),
                "profileImageUrl", userProfile.getProfileImageUrl(),
                "followerCount", userProfile.getFollowerCount(),
                "followingCount", userProfile.getFollowingCount()
        ));
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<Map<String, Object>> getUserProfileById(@PathVariable Long userId) {
        log.info("사용자 프로필 조회 요청 - 대상 ID: {}", userId);

        UserDto userProfile = userService.findById(userId)
                .orElseThrow(() -> {
                    log.warn("프로필 조회 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new IllegalArgumentException("사용자 정보를 찾을 수 없습니다.");
                });

        log.info("프로필 조회 성공 - ID: {}, 닉네임: {}", userProfile.getId(), userProfile.getNickname());

        return ApiResponseUtil.success("프로필 조회 성공", Map.of(
                "id", userProfile.getId(),
                "name", userProfile.getName(),
                "nickname", userProfile.getNickname(),
                "profileImageUrl", userProfile.getProfileImageUrl(),
                "followerCount", userProfile.getFollowerCount(),
                "followingCount", userProfile.getFollowingCount()
        ));
    }

    @PatchMapping("/nickname")
    public ResponseEntity<Map<String, Object>> updateNickname(
            CurrentUser currentUser,
            @RequestBody @Valid UpdateNicknameRequest updateRequest) {
        log.info("닉네임 변경 요청");

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("인증되지 않은 사용자입니다.");
        }

        UserDto updatedUser = userService.updateNickname(currentUser.getId(), updateRequest.getNickname());

        log.info("닉네임 변경 성공 - ID = {}, 새로운 닉네임 = {}", updatedUser.getId(), updatedUser.getNickname());

        return ApiResponseUtil.success("닉네임이 성공적으로 변경되었습니다", updatedUser);
    }

    @PatchMapping("/password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            CurrentUser currentUser,
            @RequestBody @Valid UpdatePasswordRequest updateRequest) {
        log.info("비밀번호 변경 요청");

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("인증이 필요합니다.");
        }

        UserDto updatedUser = userService.updatePassword(currentUser.getId(), updateRequest.getNewPassword());

        log.info("비밀번호 변경 성공 - ID: {}", currentUser.getId());

        return ApiResponseUtil.success("비밀번호가 성공적으로 변경되었습니다.", updatedUser);
    }

    @PatchMapping("/profile-image")
    public ResponseEntity<Map<String, Object>> updateProfileImage(
            CurrentUser currentUser,
            @RequestBody @Valid UpdateProfileImageRequest updateRequest) {
        log.info("프로필 이미지 업데이트 요청");

        if(!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("인증이 필요합니다.");
        }

        UserDto updatedUser = userService.updateProfileImage(
                currentUser.getId(), updateRequest.getProfileImageUrl()
        );

        log.info("프로필 이미지 업데이트 성공 - ID: {}", currentUser.getId());

        return ApiResponseUtil.success("프로필 이미지가 성공적으로 업데이트 되었습니다.", updatedUser);
    }

    @DeleteMapping("/profile-image")
    public ResponseEntity<Map<String, Object>> removeProfileImage(CurrentUser currentUser) {
        log.info("프로필 이미지 삭제 요청");

        if(!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("인증이 필요합니다.");
        }

        UserDto updatedUser = userService.removeProfileImage(currentUser.getId());

        log.info("프로필 이미지 삭제 완료");

        return ApiResponseUtil.success("프로필 이미지가 삭제되었습니다", updatedUser);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Map<String, Object>> deleteUser(CurrentUser currentUser) {
        log.info("회원 탈퇴 요청");

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("인증이 필요합니다.");
        }

        userService.deleteUser(currentUser.getId());

        log.info("회원 탈퇴 성공 - ID: {}, Email: {}", currentUser.getId(), currentUser.getEmail());

        return ApiResponseUtil.success("회원 탈퇴가 완료되었습니다.");
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testAuth(CurrentUser currentUser) {
        log.info("JWT 인증 테스트 요청");

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("인증 실패");
        }

        Map<String, Object> testData = Map.of(
                "userId", currentUser.getId(),
                "timestamp", System.currentTimeMillis()
        );

        log.info("JWT 인증 테스트 성공 - 사용자 ID: {}", currentUser.getId());
        return ApiResponseUtil.success("JWT 인증 성공!", testData);
    }
}