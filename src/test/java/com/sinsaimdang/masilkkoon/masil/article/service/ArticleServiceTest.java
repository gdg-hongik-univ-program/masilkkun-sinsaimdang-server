package com.sinsaimdang.masilkkoon.masil.article.service;

import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleResponse;
import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleSearchCondition;
import com.sinsaimdang.masilkkoon.masil.article.entity.Article;
import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleTag;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.entity.UserRole;
import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    private User testUser1;
    private User testUser2;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // 필터링 테스트를 위해 2명의 사용자와 4개의 게시글을 생성합니다.
        testUser1 = User.builder().email("user1@test.com").password("pw").name("유저1").nickname("서울맛집러").role(UserRole.USER).build();
        testUser2 = User.builder().email("user2@test.com").password("pw").name("유저2").nickname("부산여행가").role(UserRole.USER).build();
        adminUser = User.builder().email("admin@test.com").password("pw").name("관리자").nickname("관리자님").role(UserRole.ADMIN).build();
        userRepository.saveAll(List.of(testUser1, testUser2, adminUser));

        Article article1 = new Article("서울 맛집", "내용1", testUser1, "서울", Set.of(ArticleTag.RESTAURANT), null, null);
        Article article2 = new Article("서울 카페", "내용2", testUser1, "서울", Set.of(ArticleTag.CAFE), null, null);
        Article article3 = new Article("부산 맛집", "내용3", testUser2, "부산", Set.of(ArticleTag.RESTAURANT), null, null);
        Article article4 = new Article("부산 여행", "내용4", testUser2, "부산", Set.of(ArticleTag.TRAVEL_SPOT, ArticleTag.RESTAURANT), null, null);
        em.persist(article1);
        em.persist(article2);
        em.persist(article3);
        em.persist(article4);

        em.flush();
        em.clear();
    }

    // =================== 기존 조회 테스트 (수정됨) ===================
    @Test
    @DisplayName("조건 없이 전체 게시글 목록 조회 성공")
    void searchArticles_noCondition() {
        ArticleSearchCondition condition = new ArticleSearchCondition();
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<ArticleResponse> result = articleService.searchArticles(condition, pageRequest, UserRole.USER.name());

        assertThat(result.getTotalElements()).isEqualTo(4);
    }

    // ... (기존의 findById, 조회수 증가 등 다른 테스트는 그대로 둡니다) ...

    // =================== 새로운 필터링 테스트 ===================

    @Test
    @DisplayName("지역으로 필터링 조회")
    void searchArticles_byRegion() {
        // given
        ArticleSearchCondition condition = new ArticleSearchCondition();
        condition.setRegion("서울");
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ArticleResponse> result = articleService.searchArticles(condition, pageRequest, UserRole.USER.name());

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting(ArticleResponse::getTitle).containsExactlyInAnyOrder("서울 맛집", "서울 카페");
    }

    @Test
    @DisplayName("단일 태그로 필터링 조회 (CAFE)")
    void searchArticles_bySingleTag() {
        // given
        ArticleSearchCondition condition = new ArticleSearchCondition();
        condition.setTags(List.of(ArticleTag.CAFE));
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ArticleResponse> result = articleService.searchArticles(condition, pageRequest, UserRole.USER.name());

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("서울 카페");
    }

    @Test
    @DisplayName("다중 태그로 필터링 조회 (RESTAURANT)")
    void searchArticles_byMultipleTags() {
        // given
        ArticleSearchCondition condition = new ArticleSearchCondition();
        condition.setTags(List.of(ArticleTag.RESTAURANT)); // RESTAURANT 태그를 가진 모든 게시글
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ArticleResponse> result = articleService.searchArticles(condition, pageRequest, UserRole.USER.name());

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).extracting(ArticleResponse::getTitle)
                .containsExactlyInAnyOrder("서울 맛집", "부산 맛집", "부산 여행");
    }

    @Test
    @DisplayName("지역과 태그로 필터링 조회 (부산 & RESTAURANT)")
    void searchArticles_byRegionAndTags() {
        // given
        ArticleSearchCondition condition = new ArticleSearchCondition();
        condition.setRegion("부산");
        condition.setTags(List.of(ArticleTag.RESTAURANT));
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ArticleResponse> result = articleService.searchArticles(condition, pageRequest, UserRole.USER.name());

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting(ArticleResponse::getTitle)
                .containsExactlyInAnyOrder("부산 맛집", "부산 여행");
    }

    @Test
    @DisplayName("관리자 역할로 지역 검색 시 모든 지역 게시글 조회")
    void searchArticles_byAdminRole_IgnoresRegionFilter() {
        // given
        ArticleSearchCondition condition = new ArticleSearchCondition();
        condition.setRegion("서울"); // 서울 지역을 조건으로 설정하지만, ADMIN 역할이므로 무시되어야 함
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ArticleResponse> result = articleService.searchArticles(condition, pageRequest, UserRole.ADMIN.name());

        // then
        // 모든 게시글 4개가 조회되어야 함 (지역 필터링 무시)
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getContent()).extracting(ArticleResponse::getTitle)
                .containsExactlyInAnyOrder("서울 맛집", "서울 카페", "부산 맛집", "부산 여행");
    }
}