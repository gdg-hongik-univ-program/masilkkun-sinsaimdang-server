package com.sinsaimdang.masilkkoon.masil.article.dto;

import com.sinsaimdang.masilkkoon.masil.article.entity.Article; // Article 엔티티 임포트
import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleTag; // ArticleTag Enum 임포트
import java.time.LocalDateTime; // LocalDateTime 임포트
import java.util.Comparator; // Comparator 임포트 (정렬을 위해)
import java.util.List; // List 임포트
import java.util.stream.Collectors; // Stream API를 위한 Collectors 임포트

import lombok.Getter; // Lombok Getter
import lombok.Setter; // Lombok Setter

@Getter @Setter // Lombok Getter, Setter 자동 생성 (불변성 고려 시 Setter 제거 가능)
public class ArticleResponse {

    private Long id; // 게시글 ID
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String authorName; // 작성자 이름

    private String region; // 지역
    private List<ArticleTag> tags; // 태그 목록 (Enum 타입 그대로 반환)
    private List<String> photos; // 게시한 사진 URL 목록

    private int scrapCount; // 스크랩 수
    private int likeCount; // 좋아요 수
    private int viewCount; // 조회수

    private List<ArticlePlaceResponse> places; // 장소 경로 목록 (ArticlePlaceResponse DTO 리스트)

    private LocalDateTime createdAt; // 게시글 생성 시간
    private LocalDateTime updatedAt; // 게시글 마지막 수정 시간

    // == 생성자 (Article 엔티티를 ArticleResponse DTO로 변환) == //
    public ArticleResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.authorName = article.getAuthorName(); // 새로운 필드 반영
        this.region = article.getRegion(); // 새로운 필드 반영
        this.tags = article.getArticleTags().stream().distinct().collect(Collectors.toList()); // 태그 중복 제거
        this.photos = article.getPhotos().stream().distinct().collect(Collectors.toList()); // 사진 URL 중복 제거
        this.scrapCount = article.getScrapCount(); // 새로운 필드 반영
        this.likeCount = article.getLikeCount(); // 새로운 필드 반영
        this.viewCount = article.getViewCount(); // 기존 필드
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();

        // ArticlePlace 엔티티 컬렉션을 ArticlePlaceResponse DTO 컬렉션으로 변환
        // 1. map: DTO로 변환
        // 2. distinct: 내용 기반으로 중복 제거 (@EqualsAndHashCode 필요)
        // 3. sorted: placeOrder를 기준으로 오름차순 정렬
        // 4. collect: 최종적으로 List로 수집
        this.places = article.getArticlePlaces().stream()
                .map(ArticlePlaceResponse::new)
                .distinct()
                .sorted(Comparator.comparingInt(ArticlePlaceResponse::getPlaceOrder))
                .collect(Collectors.toList());
    }

    // 기본 생성자 (필요시)
    public ArticleResponse() {
    }
}