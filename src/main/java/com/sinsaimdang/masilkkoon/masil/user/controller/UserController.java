package com.sinsaimdang.masilkkoon.masil.user.controller;

import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleResponse;
import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleSearchCondition;
import com.sinsaimdang.masilkkoon.masil.article.service.ArticleService;
import com.sinsaimdang.masilkkoon.masil.auth.dto.CurrentUser;
import com.sinsaimdang.masilkkoon.masil.common.util.ApiResponseUtil;
import com.sinsaimdang.masilkkoon.masil.user.dto.request.UpdateNicknameRequest;
import com.sinsaimdang.masilkkoon.masil.user.dto.request.UpdatePasswordRequest;
import com.sinsaimdang.masilkkoon.masil.user.dto.UserDto;
import com.sinsaimdang.masilkkoon.masil.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 사용자 관리 Controller
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ArticleService articleService;

    /**
     * 현재 로그인한 사용자의 정보를 조회합니다.
     *
     * @param currentUser 현재 사용자 정보
     * @return 사용자 정보 DTO
     */
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

    /**
     * 특정 사용자의 프로필 정보를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 사용자 정보 DTO
     */
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

    /**
     * 사용자의 닉네임을 변경합니다.
     *
     * @param currentUser 현재 사용자 정보
     * @param updateRequest 닉네임 변경 요청 DTO
     * @return 수정된 사용자 정보 DTO
     */
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

    /**
     * 사용자의 비밀번호를 변경합니다.
     *
     * @param currentUser 현재 사용자 정보
     * @param updateRequest 비밀번호 변경 요청 DTO
     * @return 수정된 사용자 정보 DTO
     */
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

//    @PatchMapping("/profile-image")
//    public ResponseEntity<Map<String, Object>> updateProfileImage(
//            CurrentUser currentUser,
//            @RequestBody @Valid UpdateProfileImageRequest updateRequest) {
//        log.info("API REQ >> PATCH /api/user/profile-image | 요청자 ID: {}", currentUser.getId());
//
//        if(!currentUser.isAuthenticated()) {
//            return ApiResponseUtil.unauthorized("인증이 필요합니다.");
//        }
//
//        UserDto updatedUser = userService.updateProfileImage(
//                currentUser.getId(), updateRequest.getProfileImageUrl()
//        );
//
//        log.info("API RES >> PATCH /api/user/profile-image | 요청자 ID: {}", currentUser.getId());
//        return ApiResponseUtil.success("프로필 이미지가 성공적으로 업데이트 되었습니다.", updatedUser);
//    }

    /**
     * [수정] 프로필 이미지 업로드 API 변경
     * @param currentUser 현재 사용자 정보
     * @param profileImageFile 업로드할 이미지 파일
     * @return 수정된 사용자 정보 DTO
     * @throws IOException 파일 처리 중 발생할 수 있는 예외
     */
    @PostMapping("/profile-image")
    public ResponseEntity<Map<String, Object>> updateProfileImage(
            CurrentUser currentUser,
            @RequestParam("profileImage") MultipartFile profileImageFile) throws IOException {
        log.info("API REQ >> POST /api/user/profile-image | 요청자 ID: {}", currentUser.getId());

        if(!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("인증이 필요합니다.");
        }

        if (profileImageFile.isEmpty()) {
            return ApiResponseUtil.badRequest("업로드할 이미지를 선택해주세요.");
        }

        UserDto updatedUser = userService.updateProfileImage(currentUser.getId(), profileImageFile);

        log.info("API RES >> POST /api/user/profile-image | 요청자 ID: {}", currentUser.getId());
        return ApiResponseUtil.success("프로필 이미지가 성공적으로 업데이트 되었습니다.", updatedUser);
    }

    /**
     * 사용자의 프로필 이미지를 삭제합니다.
     *
     * @param currentUser 현재 사용자 정보
     * @return 수정된 사용자 정보 DTO
     */
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

    /**
     * 회원 탈퇴를 처리합니다.
     *
     * @param currentUser 현재 사용자 정보
     * @return 성공 여부
     */
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

    /**
     * 현재 로그인한 사용자가 스크랩한 게시글 목록을 조회합니다.
     *
     * @param currentUser 현재 사용자 정보
     * @param condition 검색 조건
     * @param pageable 페이징 정보
     * @return 스크랩한 게시글 DTO 목록
     */
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

    /**
     * 특정 사용자가 작성한 게시글 목록을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @param pageable 페이징 정보
     * @return 해당 사용자가 작성한 게시글 DTO 목록
     */
    @GetMapping("/{userId}/articles")
    public ResponseEntity<Map<String, Object>> getArticlesByUserId(
            @PathVariable Long userId,
            Pageable pageable,
            CurrentUser currentUser) {

        log.info("API REQ >> GET /api/user/{}/articles | 요청자 ID: {}", userId, currentUser.getId());

        try {
            Page<ArticleResponse> userArticles = articleService.findArticlesByUserId(userId, currentUser.getId(), pageable);

            log.info("API RES >> GET /api/user/{}/articles | 조회된 게시글 수: {}",
                    userId, userArticles.getContent().size());

            return ApiResponseUtil.success("작성한 게시글 목록 조회 성공", userArticles);

        } catch (IllegalArgumentException e) {
            log.warn("작성한 게시글 목록 조회 실패 - 사용자 ID: {}, 사유: {}", userId, e.getMessage());
            return ApiResponseUtil.badRequest(e.getMessage());
        }
    }

}