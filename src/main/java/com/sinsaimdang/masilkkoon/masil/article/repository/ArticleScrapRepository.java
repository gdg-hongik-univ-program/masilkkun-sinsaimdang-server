package com.sinsaimdang.masilkkoon.masil.article.repository;

import com.sinsaimdang.masilkkoon.masil.article.entity.Article;
import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArticleScrapRepository extends JpaRepository<ArticleScrap, Long> {

    boolean existsByUserIdAAndArticleId(Long userId, Long articleId);

    Optional<ArticleScrap> findByUserIdAndArticleId(Long userId, Long articleId);

    @Query("SELECT s.article FROM ArticleScrap s " +
            "WHERE s.user.id = :userId " +
            "ORDER BY s.createdAt DESC")
    Page<Article> findScrapedArticlesByUserId(@Param("userId") Long userId, Pageable pageable);
}
