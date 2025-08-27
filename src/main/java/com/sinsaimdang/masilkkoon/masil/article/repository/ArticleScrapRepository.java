package com.sinsaimdang.masilkkoon.masil.article.repository;

import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Set;

import java.util.Optional;

public interface ArticleScrapRepository extends JpaRepository<ArticleScrap, Long> {

    boolean existsByUserIdAndArticleId(Long userId, Long articleId);

    Optional<ArticleScrap> findByUserIdAndArticleId(Long userId, Long articleId);

    @Query("SELECT ascr.article.id FROM ArticleScrap ascr WHERE ascr.user.id = :userId AND ascr.article.id IN :articleIds")
    Set<Long> findScrappedArticleIdsByUserIdAndArticleIds(@Param("userId") Long userId, @Param("articleIds") List<Long> articleIds);

    @Modifying
    @Query("DELETE FROM ArticleScrap ascr WHERE ascr.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM ArticleScrap ascr WHERE ascr.article.id = :articleId")
    void deleteAllByArticleId(@Param("articleId") Long articleId);
}
