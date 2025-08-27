package com.sinsaimdang.masilkkoon.masil.article.repository;

import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Set;

import java.util.Optional;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    boolean existsByUserIdAndArticleId(Long userId, Long articleId);

    Optional<ArticleLike> findByUserIdAndArticleId(Long userId, Long articleId);

    @Query("SELECT al.article.id FROM ArticleLike al WHERE al.user.id = :userId AND al.article.id IN :articleIds")
    Set<Long> findLikedArticleIdsByUserIdAndArticleIds(@Param("userId") Long userId, @Param("articleIds") List<Long> articleIds);

    @Modifying
    @Query("DELETE FROM ArticleLike al WHERE al.user.id = :userId")
    void deleteAllByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM ArticleLike al WHERE al.article.id = :articleId")
    void deleteAllByArticleId(@Param("articleId") Long articleId);
}
