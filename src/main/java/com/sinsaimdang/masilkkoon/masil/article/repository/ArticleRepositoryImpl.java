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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static com.sinsaimdang.masilkkoon.masil.article.entity.QArticle.article;
import static com.sinsaimdang.masilkkoon.masil.article.entity.QArticleScrap.articleScrap;
import static com.sinsaimdang.masilkkoon.masil.user.entity.QUser.user;
import static com.sinsaimdang.masilkkoon.masil.region.entity.QRegion.region;

import com.querydsl.core.types.OrderSpecifier;

public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ArticleRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Article> search(ArticleSearchCondition condition, Pageable pageable) {
        List<Article> content = queryFactory
                .selectFrom(article).distinct()
                .from(article)
                .leftJoin(article.user, user).fetchJoin()
                .leftJoin(article.region, region).fetchJoin()
//                .leftJoin(article.articleTags).fetchJoin()
//                .leftJoin(article.articlePlaces).fetchJoin()
                .where(
                        tagsAllPresent(condition.getTags()),
                        regionFilter(condition.getRegion())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(articleSort(condition.getSortOrder()), article.createdAt.desc(), article.id.desc())
                .fetch();

        Long total = queryFactory
                .select(article.count())
                .from(article)
                .leftJoin(article.region, region)
                .where(
                        tagsAllPresent(condition.getTags()),
                        regionFilter(condition.getRegion())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression tagsAllPresent(List<ArticleTag> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return null; // 태그가 없으면 조건 적용 안 함 (모든 게시글 조회)
        }
        // 각 선택된 태그가 게시글의 태그 Set에 모두 포함되는지 확인 (AND 조건)
        // 예를 들어, tags = [CAFE, RESTAURANT] 이면,
        // (article.articleTags.contains(CAFE) AND article.articleTags.contains(RESTAURANT))
        BooleanExpression predicate = null;
        for (ArticleTag tag : tags) {
            if (predicate == null) {
                predicate = article.articleTags.contains(tag);
            } else {
                predicate = predicate.and(article.articleTags.contains(tag));
            }
        }
        return predicate;
    }

    // 지역 필터링을 Region 엔티티 기준으로 변경
    private BooleanExpression regionFilter(String selectedRegionName) {
        if (!StringUtils.hasText(selectedRegionName)) {
            return null;
        }
        // '서울'을 선택하면, '종로구', '강남구' 등 하위 지역에 속한 게시글도 찾아야 함.
        // article.region은 '종로구'이고, 그 부모(parent)의 이름이 '서울특별시'인 경우를 찾으면 됨.
        return article.region.name.eq(selectedRegionName) // '종로구' 자체를 검색한 경우
                .or(article.region.parent.name.eq(selectedRegionName)); // '서울특별시'로 검색한 경우
    }

    // 정렬 기준에 따라 OrderSpecifier를 반환하는 메서드
    private OrderSpecifier<?> articleSort(String sortOrder) {
        if (!StringUtils.hasText(sortOrder)) {
            return article.createdAt.desc(); // 기본 정렬: 최신순 (생성일 내림차순)
        }

        // 프론트엔드에서 "좋아요순" 또는 "조회수순" 등의 문자열을 받을 것을 가정
        switch (sortOrder) {
            case "좋아요순":
                return article.likeCount.desc(); // 좋아요 수 내림차순
            case "조회수순":
                return article.viewCount.desc(); // 조회수 내림차순
            case "오래된순":
                return article.createdAt.asc();
            case "스크랩순":
                return article.scrapCount.desc(); // 스크랩 수 내림차순
            default:
                return article.createdAt.desc(); // 기본 정렬: 최신순
        }
    }

    @Override
    public Page<Article> searchScrapedArticles(Long userId, ArticleSearchCondition condition, Pageable pageable) {
        List<Article> content = queryFactory
                .select(article)
                .from(articleScrap)
                .join(articleScrap.article, article)
                .leftJoin(article.user, user).fetchJoin()
                .leftJoin(article.region, region).fetchJoin()
                .where(
                        articleScrap.user.id.eq(userId),
                        regionFilter(condition.getRegion()),
                        tagsAllPresent(condition.getTags())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(articleSort(condition.getSortOrder()), articleScrap.createdAt.desc()) // 기존 정렬 로직도 재사용!
                .fetch();

        Long total = queryFactory
                .select(articleScrap.count())
                .from(articleScrap)
                .join(articleScrap.article, article)
                .where(
                        articleScrap.user.id.eq(userId),
                        regionFilter(condition.getRegion()),
                        tagsAllPresent(condition.getTags())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}