package com.sinsaimdang.masilkkoon.masil.article.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleSearchCondition;
import com.sinsaimdang.masilkkoon.masil.article.entity.Article;
import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleTag;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.sinsaimdang.masilkkoon.masil.article.entity.QArticle.article;
import static com.sinsaimdang.masilkkoon.masil.user.entity.QUser.user;

public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ArticleRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Article> search(ArticleSearchCondition condition, Pageable pageable) {
        List<Article> content = queryFactory
                .selectFrom(article)
                .leftJoin(article.user, user).fetchJoin()
                .where(
                        tagsIn(condition.getTags()),
                        regionEq(condition.getRegion())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(article.createdAt.desc())
                .fetch();

        long total = queryFactory
                .selectFrom(article)
                .where(
                        tagsIn(condition.getTags()),
                        regionEq(condition.getRegion())
                )
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression tagsIn(List<ArticleTag> tags) {
        return !CollectionUtils.isEmpty(tags) ? article.articleTags.any().in(tags) : null;
    }

    private BooleanExpression regionEq(String region) {
        return StringUtils.hasText(region) ? article.region.eq(region) : null;
    }
}