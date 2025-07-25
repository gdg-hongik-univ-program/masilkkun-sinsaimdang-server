package com.sinsaimdang.masilkkoon.masil.article.service;

import com.sinsaimdang.masilkkoon.masil.article.entity.Article;
import com.sinsaimdang.masilkkoon.masil.article.repository.ArticleRepository;
import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List; // 이 임포트는 더 이상 직접 사용되지 않지만, ArticleResponse의 반환 타입으로 사용될 수 있으므로 일단 유지
import java.util.Set; // Set 임포트 추가 (findAllArticles()의 내부 변환에서 사용)
import java.util.stream.Collectors; // Stream API를 위한 Collectors 임포트

import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service // 스프링 빈으로 등록
@RequiredArgsConstructor // final 필드를 이용한 생성자 자동 생성 (의존성 주입)
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 설정 (조회 기능에 적합하며 성능 향상)
public class ArticleService {

    private final ArticleRepository articleRepository; // ArticleRepository 주입

    /**
     * 모든 게시글 목록 조회 (N+1 문제 해결을 위해 Fetch Join 적용)
     * @return 게시글 DTO 목록
     */
    public List<ArticleResponse> findAllArticles() { // 메서드 반환 타입은 List 유지 (API 스펙)
        // ArticleRepository의 findAllWithCollections() 메서드를 호출하여
        // Article 엔티티와 연관된 컬렉션들을 Fetch Join으로 한 번에 가져옵니다.
        List<Article> articles = articleRepository.findAllWithCollections();

        // 조회된 Article 엔티티 리스트를 ArticleResponse DTO 리스트로 변환
        // ArticleResponse의 생성자가 Set<ArticleTag>, Set<String>, Set<ArticlePlaceResponse>를 받으므로,
        // 여기서 Collectors.toList()를 사용해도 문제는 없지만, Set의 특성(중복 없음)을 유지하려면 toSet()을 사용하는 것이 더 적절할 수 있습니다.
        // API 반환 스펙이 List<ArticleResponse> 이므로, 최종적으로는 toList()가 맞습니다.
        return articles.stream()
                .map(ArticleResponse::new)
                .collect(Collectors.toList()); // 최종 API 반환 스펙이 List이므로 toList() 유지
    }

    /**
     * 특정 ID의 게시글 단건 조회 (N+1 문제 해결을 위해 Fetch Join 적용)
     * @param articleId 조회할 게시글 ID
     * @return 게시글 DTO
     * @throws IllegalArgumentException 해당 ID의 게시글을 찾을 수 없을 경우
     */
    @Transactional // 조회수 증가 로직 때문에 별도 트랜잭션 필요
    public ArticleResponse findArticleById(Long articleId) {
        // ArticleRepository의 findByIdWithCollections() 메서드를 호출하여
        // Article 엔티티와 연관된 컬렉션들을 Fetch Join으로 한 번에 가져옵니다.
        Article article = articleRepository.findByIdWithCollections(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id: " + articleId));

        // 조회수 증가 비즈니스 로직
        article.incrementViewCount(); // Article 엔티티 내부의 조회수 증가 메서드 호출

        // 조회된 Article 엔티티를 ArticleResponse DTO로 변환
        return new ArticleResponse(article);
    }

    // TODO: 게시글 생성, 수정, 삭제 등의 비즈니스 로직은 나중에 추가
    /*
    @Transactional
    public ArticleResponse createArticle(ArticleCreateRequest request, User currentUser) {
        // ArticleCreateRequest로부터 데이터를 받아오고,
        // 현재 로그인한 사용자(currentUser) 정보를 사용하여 Article 엔티티를 생성합니다.
        Article article = new Article(
            request.getTitle(),
            request.getContent(),
            currentUser, // User 객체를 직접 전달
            request.getRegion(),
            new HashSet<>(request.getTags()),
            new HashSet<>(request.getPhotos()),
            new HashSet<>(request.getPlaces())
        );
        Article savedArticle = articleRepository.save(article);
        // 저장된 엔티티를 다시 DTO로 변환하여 반환
        return new ArticleResponse(savedArticle);
    }
    */

    @Transactional(readOnly = true)
    public Page<ArticleResponse> searchArticles(ArticleSearchCondition condition, Pageable pageable) {
        return articleRepository.search(condition, pageable).map(ArticleResponse::new);
    }
}