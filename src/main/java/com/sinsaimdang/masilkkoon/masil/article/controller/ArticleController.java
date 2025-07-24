package com.sinsaimdang.masilkkoon.masil.article.controller;

import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleResponse; // ArticleResponse DTO 임포트
import com.sinsaimdang.masilkkoon.masil.article.service.ArticleService; // ArticleService 임포트
import lombok.RequiredArgsConstructor; // Lombok RequiredArgsConstructor 임포트
import org.springframework.http.ResponseEntity; // ResponseEntity 임포트 (HTTP 응답을 유연하게 제어)
import org.springframework.web.bind.annotation.GetMapping; // GET 요청 매핑 어노테이션
import org.springframework.web.bind.annotation.PathVariable; // URL 경로 변수 매핑 어노테이션
import org.springframework.web.bind.annotation.RequestMapping; // 요청 매핑 어노테이션
import org.springframework.web.bind.annotation.RestController; // REST Controller 어노테이션 (JSON 응답)

import java.util.List; // List 임포트

@RestController // 이 클래스가 REST API를 처리하는 컨트롤러임을 명시 (JSON 응답 자동 변환)
@RequiredArgsConstructor // final 필드를 이용한 생성자 자동 생성 (의존성 주입)
@RequestMapping("/api/articles") // 이 컨트롤러의 모든 API 경로가 /api/articles로 시작함을 명시
public class ArticleController {

    private final ArticleService articleService; // ArticleService 주입

    /**
     * 모든 게시글 목록을 조회하는 API
     * GET /api/articles
     * @return 게시글 DTO 목록과 함께 HTTP 200 OK 응답
     */
    @GetMapping // /api/articles 경로의 GET 요청 처리
    public ResponseEntity<List<ArticleResponse>> getArticles() {
        List<ArticleResponse> articles = articleService.findAllArticles(); // 서비스 계층 호출
        return ResponseEntity.ok(articles); // HTTP 200 OK 응답과 함께 게시글 목록 반환
    }

    /**
     * 특정 ID의 게시글을 단건 조회하는 API
     * GET /api/articles/{articleId}
     * @param articleId 조회할 게시글의 ID
     * @return 게시글 DTO와 함께 HTTP 200 OK 응답
     */
    @GetMapping("/{articleId}") // /api/articles/{articleId} 경로의 GET 요청 처리
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable Long articleId) {
        ArticleResponse article = articleService.findArticleById(articleId); // 서비스 계층 호출
        return ResponseEntity.ok(article); // HTTP 200 OK 응답과 함께 단건 게시글 반환
    }

    // TODO: 게시글 생성, 수정, 삭제 API는 나중에 추가 (Phase 1에서는 조회만 집중)
}