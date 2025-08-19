package com.sinsaimdang.masilkkoon.masil.article.controller;

import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleResponse; // ArticleResponse DTO 임포트
import com.sinsaimdang.masilkkoon.masil.article.service.ArticleService; // ArticleService 임포트
import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleCreateRequest;
import com.sinsaimdang.masilkkoon.masil.auth.dto.CurrentUser;
import com.sinsaimdang.masilkkoon.masil.common.util.ApiResponseUtil;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleUpdateRequest;

import lombok.RequiredArgsConstructor; // Lombok RequiredArgsConstructor 임포트
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus; // HTTP 상태 코드를 위한 HttpStatus 임포트
import org.springframework.http.ResponseEntity; // ResponseEntity 임포트 (HTTP 응답을 유연하게 제어)
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.GetMapping; // GET 요청 매핑 어노테이션
//import org.springframework.web.bind.annotation.PathVariable; // URL 경로 변수 매핑 어노테이션
//import org.springframework.web.bind.annotation.RequestMapping; // 요청 매핑 어노테이션
//import org.springframework.web.bind.annotation.RestController; // REST Controller 어노테이션 (JSON 응답)
import jakarta.servlet.http.HttpServletRequest; // HttpServletRequest 임포트

//import java.util.List; // List 임포트

import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import java.util.HashMap; // Map 응답을 위한 HashMap 임포트
import java.util.Map;     // Map 응답을 위한 Map 임포트

@RestController // 이 클래스가 REST API를 처리하는 컨트롤러임을 명시 (JSON 응답 자동 변환)
@RequiredArgsConstructor // final 필드를 이용한 생성자 자동 생성 (의존성 주입)
@RequestMapping("/api/articles") // 이 컨트롤러의 모든 API 경로가 /api/articles로 시작함을 명시
@Slf4j
public class ArticleController {

    private final ArticleService articleService; // ArticleService 주입
    private final UserRepository userRepository; // ✨ UserRepository 주입

//    /**
//     * 모든 게시글 목록을 조회하는 API
//     * GET /api/articles
//     * @return 게시글 DTO 목록과 함께 HTTP 200 OK 응답
//     */
//    @GetMapping // /api/articles 경로의 GET 요청 처리
//    public ResponseEntity<List<ArticleResponse>> getArticles() {
//        List<ArticleResponse> articles = articleService.findAllArticles(); // 서비스 계층 호출
//        return ResponseEntity.ok(articles); // HTTP 200 OK 응답과 함께 게시글 목록 반환
//    }

    /**
     * 특정 ID의 게시글을 단건 조회하는 API
     * GET /api/articles/{articleId}
     * @param articleId 조회할 게시글의 ID
     * @param request HttpServletRequest를 통해 사용자 역할 정보를 얻음
     * @return 게시글 DTO와 함께 HTTP 200 OK 응답 또는 오류 응답 (표준 응답 형식)
     */
    @GetMapping("/{articleId}")
    public ResponseEntity<Map<String, Object>> getArticle( // 반환 타입을 Map<String, Object>로 변경
                                                           @PathVariable Long articleId,
                                                           HttpServletRequest request) { // HttpServletRequest를 통해 요청 속성 접근

        log.info("게시글 단건 조회 요청 - ID: {}", articleId); // 요청 로깅
        try {
            // JWT 필터에서 설정한 사용자 역할 정보 추출
            // 인증되지 않은 사용자라면 userRole은 null이 됩니다.
            String userRole = (String) request.getAttribute("currentUserRole");

            // 서비스 계층 호출 시 userRole 전달
            ArticleResponse article = articleService.findArticleById(articleId, userRole);

            log.info("게시글 단건 조회 요청 완료");
            // 표준 응답 형식에 맞춰 Map 생성 및 반환
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "게시글 조회 성공");
            response.put("data", article); // 단일 ArticleResponse 객체
            return ResponseEntity.ok(response); // HTTP 200 OK

        } catch (IllegalArgumentException e) {
            log.warn("게시글 단건 조회 실패 - ID: {}, 사유: {}", articleId, e.getMessage()); // 경고 로깅
            // 표준 오류 응답 형식에 맞춰 Map 생성 및 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404 Not Found
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 단건 조회 중 서버 오류 발생 - ID: {}", articleId, e); // 에러 로깅
            // 표준 오류 응답 형식에 맞춰 Map 생성 및 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 Internal Server Error
                    .body(Map.of("success", false, "message", "서버 내부 오류가 발생했습니다."));
        }
    }

    /**
     * 게시글 목록을 필터링하여 조회하는 API
     * GET /api/articles?region=...&tags=...&page=...&size=...
     * @param condition 검색 조건 (지역, 태그)
     * @param pageable 페이징 정보
     * @param request HttpServletRequest를 통해 사용자 역할 정보를 얻음
     * @return 필터링된 게시글 목록 (페이지네이션 포함)과 함께 HTTP 200 OK 응답 또는 오류 응답 (표준 응답 형식)
     */
    @GetMapping // 필터링
    public ResponseEntity<Map<String, Object>> getArticles( // 반환 타입을 Map<String, Object>로 변경
                                                            ArticleSearchCondition condition,
                                                            Pageable pageable,
                                                            HttpServletRequest request) { // HttpServletRequest를 통해 요청 속성 접근

        // JWT 필터에서 설정한 사용자 역할 정보 추출
        // 인증되지 않은 사용자라면 userRole은 null이 됩니다.
        String userRole = (String) request.getAttribute("currentUserRole");
        log.info("게시글 목록 조회 요청 - 조건: {}, 페이징: {}, 사용자 역할: {}", condition, pageable, userRole); // 요청 로깅

        try {
            // 서비스 계층 호출 시 userRole 전달
            Page<ArticleResponse> articlesPage = articleService.searchArticles(condition, pageable, userRole);

            log.info("게시글 목록 조회 완료 - 조건: {}, 페이징: {}, 사용자 역할: {}", condition, pageable, userRole); // 요청 로깅
            // 표준 응답 형식에 맞춰 Map 생성 및 반환
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "게시글 목록 조회 성공");
            response.put("data", articlesPage); // Page 객체는 JSON으로 잘 직렬화됩니다.
            return ResponseEntity.ok(response); // HTTP 200 OK

        } catch (Exception e) {
            log.error("게시글 목록 조회 중 서버 오류 발생 - 조건: {}, 페이징: {}, 사용자 역할: {}", condition, pageable, userRole, e); // 에러 로깅
            // 표준 오류 응답 형식에 맞춰 Map 생성 및 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "서버 내부 오류가 발생했습니다."));
        }
    }

    @PostMapping("/{articleId}/likes")
    public ResponseEntity<Map<String, Object>> addlike(
            @PathVariable Long articleId,
            CurrentUser currentUser) {

        log.info("API REQ >> POST /api/articles/{}/likes | 요청자 ID: {}", articleId, currentUser.getId());

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("로그인이 필요합니다.");
        }

        articleService.addLike(currentUser.getId(), articleId);

        log.info("API RES >> POST /api/articles/{}/likes | 요청자 ID: {}", articleId, currentUser.getId());
        return ApiResponseUtil.success("게시글 좋아요 완료");
    }

    @DeleteMapping("/{articleId}/likes")
    public ResponseEntity<Map<String, Object>> removeLike(
            @PathVariable Long articleId,
            CurrentUser currentUser) {

        log.info("API REQ >> DELETE /api/articles/{}/likes | 요청자 ID: {}", articleId, currentUser.getId());

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("로그인이 필요합니다.");
        }

        articleService.removeLike(currentUser.getId(), articleId);

        log.info("API RES >> DELETE /api/articles/{}/likes | 요청자 ID: {}", articleId, currentUser.getId());
        return ApiResponseUtil.success("게시글 좋아요를 취소했습니다.");
    }

    @PostMapping("/{articleId}/scraps")
    public ResponseEntity<Map<String, Object>> addScrap(
            @PathVariable Long articleId,
            CurrentUser currentUser) {

        log.info("API REQ >> POST /api/articles/{}/scraps | 요청자 ID: {}", articleId, currentUser.getId());

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("로그인이 필요합니다.");
        }

        try {
            articleService.addScrap(currentUser.getId(), articleId);
            log.info("API RES >> POST /api/articles/{}/scraps | 요청자 ID: {}", articleId, currentUser.getId());
            return ApiResponseUtil.success("게시글 스크랩 완료");
        } catch (IllegalArgumentException e) {
            log.warn("게시글 스크랩 실패 - 게시글 ID: {}, 요청자 ID: {}, 사유: {}",
                    articleId, currentUser.getId(), e.getMessage());
            return ApiResponseUtil.badRequest(e.getMessage());
        }
    }

    @DeleteMapping("/{articleId}/scraps")
    public ResponseEntity<Map<String, Object>> removeScrap(
            @PathVariable Long articleId,
            CurrentUser currentUser) {

        log.info("API REQ >> DELETE /api/articles/{}/scraps | 요청자 ID: {}", articleId, currentUser.getId());

        if (!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("로그인이 필요합니다.");
        }

        try {
            articleService.removeScrap(currentUser.getId(), articleId);
            log.info("API RES >> DELETE /api/articles/{}/scraps | 요청자 ID: {}", articleId, currentUser.getId());
            return ApiResponseUtil.success("게시글 스크랩을 취소했습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("게시글 스크랩 취소 실패 - 게시글 ID: {}, 요청자 ID: {}, 사유: {}",
                    articleId, currentUser.getId(), e.getMessage());
            return ApiResponseUtil.badRequest(e.getMessage());
        }
    }

    /**
     * 새로운 게시글을 생성하는 API
     * POST /api/articles
     * @param request 게시글 생성 요청 데이터 DTO
     * @param currentUser 현재 로그인한 사용자 정보 (ArgumentResolver가 주입)
     * @return 생성된 게시글 DTO와 함께 HTTP 201 Created 응답 또는 오류 응답
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createArticle(
            @Valid @RequestBody ArticleCreateRequest request,
            CurrentUser currentUser) {

        // 요청 수신 및 받은 데이터(DTO) 전체를 기록
        log.info("=> 게시글 생성 요청 수신 - 요청자 ID: {}, 요청 내용: {}", currentUser.getId(), request);

        try {
            // CurrentUser에 담긴 id로 실제 User 엔티티를 데이터베이스에서 조회합니다.
            User user = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + currentUser.getId()));

            ArticleResponse createdArticle = articleService.createArticle(request, user);

            return ApiResponseUtil.created("게시글이 성공적으로 등록되었습니다.", createdArticle);

        } catch (IllegalArgumentException e) {
            log.warn("<= 게시글 생성 실패 - 사유: {}, 요청 내용: {}", e.getMessage(), request, e);
            return ApiResponseUtil.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("<= 게시글 생성 중 서버 오류 발생 - 요청 내용: {}", request, e);
            return ApiResponseUtil.internalServerError("서버 내부 오류가 발생했습니다.");
        }
    }

    /**
     * 특정 ID의 게시글을 삭제하는 API
     * DELETE /api/articles/{articleId}
     * @param articleId 삭제할 게시글의 ID
     * @param currentUser 현재 로그인한 사용자 정보
     * @return 성공 또는 실패 메시지와 함께 HTTP 응답
     */
    @DeleteMapping("/{articleId}")
    public ResponseEntity<Map<String, Object>> deleteArticle(
            @PathVariable Long articleId,
            CurrentUser currentUser) {

        log.info("게시글 삭제 요청 - 게시글 ID: {}, 요청자 ID: {}", articleId, currentUser.getId());

        try {
            // 1. 로그인된 사용자인지 확인합니다. (토큰이 유효한지)
            if (!currentUser.isAuthenticated()) {
                return ApiResponseUtil.unauthorized("인증이 필요합니다.");
            }

            // 2. 서비스 계층에 articleId와 요청자 ID를 넘겨 삭제를 위임합니다.
            articleService.deleteArticle(articleId, currentUser.getId());

            // 3. 성공 응답을 반환합니다.
            return ApiResponseUtil.success("게시글이 성공적으로 삭제되었습니다.");

        } catch (IllegalArgumentException e) { // Service에서 게시글을 못 찾았을 경우
            log.warn("<= 게시글 삭제 실패 - 사유: {}, 게시글 ID: {}", e.getMessage(), articleId, e);
            return ApiResponseUtil.badRequest(e.getMessage());
        } catch (SecurityException e) { // Service에서 권한이 없다고 판단했을 경우
            log.warn("<= 게시글 삭제 권한 없음 - 게시글 ID: {}, 요청자 ID: {}", articleId, currentUser.getId(), e);
            return ApiResponseUtil.error(HttpStatus.FORBIDDEN, e.getMessage()); // 403 Forbidden 응답
        } catch (Exception e) { // 그 외 서버 에러
            log.error("<= 게시글 삭제 중 서버 오류 발생 - 게시글 ID: {}", articleId, e);
            return ApiResponseUtil.internalServerError("서버 내부 오류가 발생했습니다.");
        }
    }

    /**
     * 특정 ID의 게시글을 수정하는 API
     * PUT /api/articles/{articleId}
     * @param articleId 수정할 게시글의 ID
     * @param request 수정할 내용이 담긴 DTO
     * @param currentUser 현재 로그인한 사용자 정보
     * @return 수정된 게시글 DTO와 함께 HTTP 200 OK 응답
     */
    @PutMapping("/{articleId}")
    public ResponseEntity<Map<String, Object>> updateArticle(
            @PathVariable Long articleId,
            @Valid @RequestBody ArticleUpdateRequest request,
            CurrentUser currentUser) {

        log.info("게시글 수정 요청 - 게시글 ID: {}, 요청자 ID: {}", articleId, currentUser.getId());

        try {
            ArticleResponse updatedArticle = articleService.updateArticle(articleId, request, currentUser.getId());
            return ApiResponseUtil.success("게시글이 성공적으로 수정되었습니다.", updatedArticle);

        } catch (IllegalArgumentException e) {
            log.warn("<= 게시글 수정 실패 - 사유: {}, 게시글 ID: {}, 요청 내용: {}", e.getMessage(), articleId, request, e);
            return ApiResponseUtil.badRequest(e.getMessage());
        } catch (SecurityException e) {
            log.warn("<= 게시글 수정 권한 없음 - 게시글 ID: {}, 요청자 ID: {}", articleId, currentUser.getId(), e);
            return ApiResponseUtil.error(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            log.error("<= 게시글 수정 중 서버 오류 발생 - 게시글 ID: {}, 요청 내용: {}", articleId, request, e);
            return ApiResponseUtil.internalServerError("서버 내부 오류가 발생했습니다.");
        }
    }

}