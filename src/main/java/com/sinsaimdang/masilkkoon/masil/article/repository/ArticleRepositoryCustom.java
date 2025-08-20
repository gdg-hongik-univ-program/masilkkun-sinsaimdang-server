package com.sinsaimdang.masilkkoon.masil.article.repository;

import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleSearchCondition;
import com.sinsaimdang.masilkkoon.masil.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleRepositoryCustom {
    Page<Article> search(ArticleSearchCondition condition, Pageable pageable);

    Page<Article> searchScrapedArticles(Long userId, ArticleSearchCondition condition, Pageable pageable);
}