package com.sinsaimdang.masilkkoon.masil.article.entity;

import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "article_scrap", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_article_scrap_user_article",
                columnNames = {"user_id", "article_id"}
        )
})
@Entity
public class ArticleScrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ArticleScrap(User user, Article article) {
        this.user = user;
        this.article = article;
    }
}
