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
import static com.sinsaimdang.masilkkoon.masil.user.entity.QUser.user;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 지역 그룹핑 정의 (예시 데이터)
    // 실제 요구사항에 맞춰 부산과 경남의 포함 관계를 명확히 정의해야 합니다.
    // 여기서는 '경남' 선택 시 '부산'과 경상남도 내의 주요 도시들을 함께 조회하는 것으로 가정합니다.
    private static final Map<String, List<String>> REGION_GROUPS = new HashMap<>();
    static {
        REGION_GROUPS.put("경남", Arrays.asList("부산", "창원", "진주", "김해", "양산", "거제", "통영", "사천", "밀양"));
        // 필요시 다른 지역 그룹도 여기에 추가할 수 있습니다.
        // REGION_GROUPS.put("수도권", Arrays.asList("서울", "인천", "경기"));
    }

    public ArticleRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Article> search(ArticleSearchCondition condition, Pageable pageable) {
        List<Article> content = queryFactory
                .selectFrom(article)
                .leftJoin(article.user, user).fetchJoin()
                .where(
                        tagsAllPresent(condition.getTags()),
                        regionFilter(condition.getRegion())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(articleSort(condition.getSortOrder()))
                .fetch();

        long total = queryFactory
                .selectFrom(article)
                .where(
                        tagsAllPresent(condition.getTags()),
                        regionFilter(condition.getRegion())
                )
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
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


    // 지역 그룹핑을 고려한 필터링 메서드
    private BooleanExpression regionFilter(String selectedRegion) {
        if (!StringUtils.hasText(selectedRegion)) {
            return null; // 선택된 지역이 없으면 조건 적용 안 함
        }

        // 선택된 지역이 그룹(예: '경남')에 해당하는지 확인
        if (REGION_GROUPS.containsKey(selectedRegion)) {
            List<String> regionsInGroup = REGION_GROUPS.get(selectedRegion);
            // 해당 그룹에 속하는 모든 지역을 포함하는 게시글 조회 (IN 조건)
            return article.region.in(regionsInGroup);
        } else {
            // 그룹이 아니면 정확히 일치하는 지역만 조회
            return article.region.eq(selectedRegion);
        }
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
            default:
                return article.createdAt.desc(); // 기본 정렬: 최신순
        }
    }
}