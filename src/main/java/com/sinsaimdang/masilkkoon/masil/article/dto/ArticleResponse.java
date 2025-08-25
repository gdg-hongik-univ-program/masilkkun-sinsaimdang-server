package com.sinsaimdang.masilkkoon.masil.article.dto;

import com.sinsaimdang.masilkkoon.masil.article.entity.Article;
import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleTag;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.entity.UserRole;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ArticleResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final AuthorDto author; // 작성자 정보를 담는 내부 DTO
    private final String region;
    private final List<ArticleTag> tags;
    private final int scrapCount;
    private final int likeCount;
    private final int viewCount;
    private final List<ArticlePlaceResponse> places;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // == 생성자 (Article 엔티티를 ArticleResponse DTO로 변환) == //
    public ArticleResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.author = new AuthorDto(article.getUser()); // User 엔티티로 AuthorDto 생성
        this.region = article.getRegion().getName();
        this.tags = article.getArticleTags().stream().distinct().collect(Collectors.toList());
        this.scrapCount = article.getScrapCount();
        this.likeCount = article.getLikeCount();
        this.viewCount = article.getViewCount();
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
        this.places = article.getArticlePlaces().stream()
                .map(ArticlePlaceResponse::new)
                .distinct()
                .sorted(Comparator.comparingInt(ArticlePlaceResponse::getPlaceOrder))
                .collect(Collectors.toList());
    }

    // == 작성자 정보를 담는 내부 DTO 클래스 == //
    @Getter
    public static class AuthorDto {
        private final Long id;
        private final String nickname;
        private final UserRole role;
        private final String profileImageUrl;

        public AuthorDto(User user) {
            this.id = user.getId();
            this.nickname = user.getNickname();
            this.role = user.getRole();
            this.profileImageUrl = user.getProfileImageUrl();
        }
    }
}
