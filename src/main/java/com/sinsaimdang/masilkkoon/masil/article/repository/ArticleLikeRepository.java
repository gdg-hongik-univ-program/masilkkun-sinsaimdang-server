package com.sinsaimdang.masilkkoon.masil.article.repository;

import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    boolean existsByUserIdAndArticleId(Long userId, Long articleId);

    Optional<ArticleLike> findByUserIdAndArticleId(Long userId, Long articleId);
}
