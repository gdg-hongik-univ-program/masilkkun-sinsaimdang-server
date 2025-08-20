package com.sinsaimdang.masilkkoon.masil.article.repository;

import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleSearchCondition;
import com.sinsaimdang.masilkkoon.masil.article.entity.Article;
import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArticleScrapRepository extends JpaRepository<ArticleScrap, Long> {

    boolean existsByUserIdAndArticleId(Long userId, Long articleId);

    Optional<ArticleScrap> findByUserIdAndArticleId(Long userId, Long articleId);

    @Query("SELECT s.article FROM ArticleScrap s " +
            "JOIN s.article a " +
            "WHERE s.user.id = :userId " +
            "AND (:#{#condition.region} IS NULL OR a.region = :#{#condition.region}) " +
            "AND (:#{#condition.tags} IS NULL OR :#{#condition.tags} IS EMPTY OR " +
            "     (SELECT COUNT(tag) FROM a.articleTags tag WHERE tag IN :#{#condition.tags}) = :#{#condition.tags?.size()}) " +
            "ORDER BY s.createdAt DESC")
    Page<Article> findScrapedArticlesByUserId(@Param("userId") Long userId,
                                              @Param("condition") ArticleSearchCondition condition,
                                              Pageable pageable);

    @Modifying
    @Query("DELETE FROM ArticleScrap ascr WHERE ascr.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
