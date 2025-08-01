package com.sinsaimdang.masilkkoon.masil.article.entity;

import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import jakarta.persistence.*; // JPA 관련 어노테이션
import lombok.Getter; // Lombok Getter
import lombok.Setter; // Lombok Setter
import java.time.LocalDateTime; // 생성일, 수정일을 위한 LocalDateTime
import java.util.HashSet;
import java.util.Set;

@Entity // 이 클래스가 JPA 엔티티임을 명시
@Table(name = "articles") // 데이터베이스 테이블 이름 지정 (관례상 소문자 복수형)
@Getter // Lombok을 사용하여 Getter 자동 생성
@Setter // Lombok을 사용하여 Setter 자동 생성 (초보 단계에서 편의상 사용)
public class Article {

    @Id // 기본 키(Primary Key)임을 명시
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 ID를 자동 생성 (MySQL의 auto_increment)
    @Column(name = "article_id") // DB 컬럼명 지정
    private Long id;

    @Column(nullable = false, length = 255)
    private String title; // 게시글 제목

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 게시글 내용

    // User 엔티티와 다대일(Many-to-One) 관계 설정
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 성능 최적화
    @JoinColumn(name = "user_id", nullable = false) // DB의 user_id 컬럼과 매핑
    private User user; // 작성자 정보를 User 객체로 관리

    @Column(nullable = false, length = 50) // 10개 지역 중 하나
    private String region; // 지역

    // 태그: Enum 컬렉션을 DB에 저장하기 위해 @ElementCollection 사용
    // List 대신 Set을 사용하여 MultipleBagFetchException 회피 및 중복 방지
    @ElementCollection(fetch = FetchType.LAZY) // 지연 로딩
    @CollectionTable(name = "article_tags", joinColumns = @JoinColumn(name = "article_id"))
    @Enumerated(EnumType.STRING) // Enum 이름을 문자열로 DB에 저장
    @Column(name = "tag") // 태그 값을 저장할 컬럼명
    private Set<ArticleTag> articleTags = new HashSet<>();

    // 사진 URL: URL 문자열 Set으로 저장
    @ElementCollection(fetch = FetchType.LAZY) // 지연 로딩
    @CollectionTable(name = "article_photos", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "photo_url", length = 1000) // 사진 URL을 저장할 컬럼명 (URL이 길 수 있음)
    private Set<String> photos = new HashSet<>();

    @Column(nullable = false)
    private int scrapCount = 0; // 스크랩 수

    @Column(nullable = false)
    private int likeCount = 0; // 좋아요 수

    @Column(nullable = false)
    private int viewCount = 0; // 조회수

    // 장소 경로: ArticlePlace 임베디드 타입의 Set으로 저장
    @ElementCollection(fetch = FetchType.LAZY) // 지연 로딩
    @CollectionTable(name = "article_places", joinColumns = @JoinColumn(name = "article_id"))
    private Set<ArticlePlace> articlePlaces = new HashSet<>();

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt; // 게시글 생성 시간

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 게시글 마지막 수정 시간

    // == 비즈니스 로직 메서드 == //
    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementScrapCount() {
        this.scrapCount++;
    }

    public void decrementScrapCount() {
        if (this.scrapCount > 0) {
            this.scrapCount--;
        }
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    // == 엔티티 생명주기 콜백 메서드 (Auditing) == //
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // == 생성자 (JPA용 기본 생성자 및 편의 생성자) == //
    protected Article() {
    }

    public Article(String title, String content, User user, String region,
                   Set<ArticleTag> articleTags, Set<String> photos,
                   Set<ArticlePlace> articlePlaces) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.region = region;
        this.articleTags = articleTags != null ? new HashSet<>(articleTags) : new HashSet<>();
        this.photos = photos != null ? new HashSet<>(photos) : new HashSet<>();
        this.articlePlaces = articlePlaces != null ? new HashSet<>(articlePlaces) : new HashSet<>();
        this.scrapCount = 0;
        this.likeCount = 0;
        this.viewCount = 0;
    }

    // 게시글 업데이트를 위한 비즈니스 메서드 (Setter 대신 사용 권장)
    public void updateArticle(String title, String content, String region,
                              Set<ArticleTag> articleTags, Set<String> photos,
                              Set<ArticlePlace> articlePlaces) {
        if (title != null && !title.isEmpty()) {
            this.title = title;
        }
        if (content != null && !content.isEmpty()) {
            this.content = content;
        }
        if (region != null && !region.isEmpty()) {
            this.region = region;
        }
        if (articleTags != null) {
            this.articleTags.clear();
            this.articleTags.addAll(articleTags);
        }
        if (photos != null) {
            this.photos.clear();
            this.photos.addAll(photos);
        }
        if (articlePlaces != null) {
            this.articlePlaces.clear();
            this.articlePlaces.addAll(articlePlaces);
        }
        // updatedAt은 @PreUpdate에서 자동 처리됨
    }
}