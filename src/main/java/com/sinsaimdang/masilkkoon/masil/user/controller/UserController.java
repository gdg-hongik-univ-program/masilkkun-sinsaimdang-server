package com.sinsaimdang.masilkkoon.masil.user.controller;

import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleResponse;
import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleSearchCondition;
import com.sinsaimdang.masilkkoon.masil.article.service.ArticleService;
import com.sinsaimdang.masilkkoon.masil.auth.dto.CurrentUser;
import com.sinsaimdang.masilkkoon.masil.common.util.ApiResponseUtil;
import com.sinsaimdang.masilkkoon.masil.user.dto.request.UpdateNicknameRequest;
import com.sinsaimdang.masilkkoon.masil.user.dto.request.UpdatePasswordRequest;
import com.sinsaimdang.masilkkoon.masil.user.dto.request.UpdateProfileImageRequest;
import com.sinsaimdang.masilkkoon.masil.user.dto.UserDto;
import com.sinsaimdang.masilkkoon.masil.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ArticleService articleService;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(CurrentUser currentUser) {
        log.info("API REQ >> GET /api/user/me | 요청자 ID: {}", currentUser.getId());

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("인증되지 않은 사용자 입니다");
        }

        UserDto userProfile = userService.findById(currentUser.getId())
                        .orElseThrow(() -> {
                            log.warn("사용자 정보 실패 - 존재하지 않는 사용자 ID : {}", currentUser.getId());
                            return new IllegalArgumentException("사용자 정보를 찾을 수 없습니다");
                        });

        log.info("API RES >> GET /api/user/me | 요청자 ID: {}", currentUser.getId());
        return ApiResponseUtil.success("사용자 정보 조회 성공", userProfile);
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<Map<String, Object>> getUserProfileById(@PathVariable Long userId) {
        log.info("API REQ >> GET /api/user/{}/profile", userId);

        UserDto userProfile = userService.findById(userId)
                .orElseThrow(() -> {
                    log.warn("프로필 조회 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new IllegalArgumentException("사용자 정보를 찾을 수 없습니다.");
                });

        log.info("API RES >> GET /api/user/{}/profile", userId);

        return ApiResponseUtil.success("프로필 조회 성공", userProfile);
    }

    @PatchMapping("/nickname")
    public ResponseEntity<Map<String, Object>> updateNickname(
            CurrentUser currentUser,
            @RequestBody @Valid UpdateNicknameRequest updateRequest) {
        log.info("API REQ >> PATCH /api/user/nickname | 요청자 ID: {}", currentUser.getId());

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("인증되지 않은 사용자입니다.");
        }

        UserDto updatedUser = userService.updateNickname(currentUser.getId(), updateRequest.getNickname());

        log.info("API RES >> PATCH /api/user/nickname | 요청자 ID: {}", currentUser.getId());
        return ApiResponseUtil.success("닉네임이 성공적으로 변경되었습니다", updatedUser);
    }

    @PatchMapping("/password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            CurrentUser currentUser,
            @RequestBody @Valid UpdatePasswordRequest updateRequest) {
        log.info("API REQ >> PATCH /api/user/password | 요청자 ID: {}", currentUser.getId());

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("인증이 필요합니다.");
        }

        UserDto updatedUser = userService.updatePassword(currentUser.getId(), updateRequest.getNewPassword());

        log.info("API RES >> PATCH /api/user/password | 요청자 ID: {}", currentUser.getId());
        return ApiResponseUtil.success("비밀번호가 성공적으로 변경되었습니다.", updatedUser);
    }

    @PatchMapping("/profile-image")
    public ResponseEntity<Map<String, Object>> updateProfileImage(
            CurrentUser currentUser,
            @RequestBody @Valid UpdateProfileImageRequest updateRequest) {
        log.info("API REQ >> PATCH /api/user/profile-image | 요청자 ID: {}", currentUser.getId());

        if(!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("인증이 필요합니다.");
        }

        UserDto updatedUser = userService.updateProfileImage(
                currentUser.getId(), updateRequest.getProfileImageUrl()
        );

        log.info("API RES >> PATCH /api/user/profile-image | 요청자 ID: {}", currentUser.getId());
        return ApiResponseUtil.success("프로필 이미지가 성공적으로 업데이트 되었습니다.", updatedUser);
    }

    @DeleteMapping("/profile-image")
    public ResponseEntity<Map<String, Object>> removeProfileImage(CurrentUser currentUser) {
        log.info("API REQ >> DELETE /api/user/profile-image | 요청자 ID: {}", currentUser.getId());

        if(!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("인증이 필요합니다.");
        }

        UserDto updatedUser = userService.removeProfileImage(currentUser.getId());

        log.info("API RES >> DELETE /api/user/profile-image | 요청자 ID: {}", currentUser.getId());
        return ApiResponseUtil.success("프로필 이미지가 삭제되었습니다", updatedUser);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Map<String, Object>> deleteUser(CurrentUser currentUser) {
        log.info("API REQ >> DELETE /api/user/me | 요청자 ID: {}", currentUser.getId());

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("인증이 필요합니다.");
        }

        userService.deleteUser(currentUser.getId());

        log.info("API RES >> DELETE /api/user/me | 요청자 ID: {}", currentUser.getId());
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

    @GetMapping("/scraps")
    public ResponseEntity<Map<String, Object>> getMyScrapedArticles(
            CurrentUser currentUser,
            ArticleSearchCondition condition,
            Pageable pageable) {

        log.info("API REQ >> GET /api/user/scraps | 요청자 ID: {}", currentUser.getId());

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("로그인이 필요합니다.");
        }

        try {
            Page<ArticleResponse> scrapedArticles = articleService.getScrapedArticles(currentUser.getId(), condition, pageable);

            log.info("API RES >> GET /api/user/scraps | 요청자 ID: {}, 조회된 게시글 수: {}",
                    currentUser.getId(), scrapedArticles.getContent().size());

            return ApiResponseUtil.success("스크랩 목록 조회 성공", scrapedArticles);
        } catch (IllegalArgumentException e) {
            log.warn("스크랩 목록 조회 실패 - 요청자 ID: {}, 사유: {}", currentUser.getId(), e.getMessage());
            return ApiResponseUtil.badRequest(e.getMessage());
        }
    }
}