package com.sinsaimdang.masilkkoon.masil.article.service;

import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleResponse;
import com.sinsaimdang.masilkkoon.masil.article.entity.Article;
import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleTag;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional // 테스트 후 데이터베이스 롤백을 보장
class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    private User testUser;

    // 각 테스트 실행 전에 테스트에 필요한 기본 데이터를 설정합니다.
    @BeforeEach
    void setUp() {
        // 테스트용 사용자(User) 생성 및 저장
        testUser = User.builder()
                .email("testuser@example.com")
                .password("password123")
                .name("테스트유저")
                .nickname("테스터")
                .build();
        userRepository.save(testUser);

        // 테스트용 게시글 2개 생성 및 저장
        Article article1 = new Article("JPA 게시글", "내용1", testUser, "서울", Set.of(ArticleTag.RESTAURANT), null, null);
        Article article2 = new Article("Spring 게시글", "내용2", testUser, "부산", Set.of(ArticleTag.CAFE), null, null);
        em.persist(article1);
        em.persist(article2);

        // 영속성 컨텍스트의 변경 내용을 DB에 반영하고 컨텍스트를 초기화하여
        // 이후의 find 테스트가 캐시가 아닌 DB에서 데이터를 읽도록 보장합니다.
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("전체 게시글 목록 조회 성공")
    void findAllArticles_Success() {
        // given: @BeforeEach에서 데이터 2건이 준비된 상태

        // when
        List<ArticleResponse> articles = articleService.findAllArticles();

        // then
        assertThat(articles).isNotNull();
        assertThat(articles.size()).isEqualTo(2);
        assertThat(articles).extracting(ArticleResponse::getTitle)
                .containsExactlyInAnyOrder("JPA 게시글", "Spring 게시글");
    }

    @Test
    @DisplayName("특정 ID 게시글 단건 조회 성공")
    void findArticleById_Success() {
        // given
        // 테스트용 게시글 추가 생성
        Article newArticle = new Article("단건조회 테스트", "내용3", testUser, "제주", Set.of(ArticleTag.TRAVEL_SPOT), null, null);
        em.persist(newArticle);
        Long articleId = newArticle.getId();
        em.flush();
        em.clear();

        // when
        ArticleResponse foundArticle = articleService.findArticleById(articleId);

        // then
        assertThat(foundArticle).isNotNull();
        assertThat(foundArticle.getId()).isEqualTo(articleId);
        assertThat(foundArticle.getTitle()).isEqualTo("단건조회 테스트");
        assertThat(foundArticle.getAuthor().getNickname()).isEqualTo(testUser.getNickname());
    }

    @Test
    @DisplayName("단건 조회 시 조회수(viewCount) 1 증가")
    void findArticleById_IncrementsViewCount() {
        // given
        Article newArticle = new Article("조회수 테스트", "내용4", testUser, "경기", null, null, null);
        em.persist(newArticle);
        Long articleId = newArticle.getId();
        em.flush();
        em.clear();

        // when
        articleService.findArticleById(articleId);

        // then
        // 영속성 컨텍스트를 거치지 않고 DB에서 직접 엔티티를 다시 조회하여 검증
        Article updatedArticle = em.find(Article.class, articleId);
        assertThat(updatedArticle.getViewCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 예외 발생")
    void findArticleById_NotFound_ThrowsException() {
        // given
        Long nonExistentId = 99999L; // 존재하지 않을 것이 확실한 ID

        // when & then
        // 해당 예외가 발생하는지 검증
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            articleService.findArticleById(nonExistentId);
        });

        // 예외 메시지까지 검증하여 더 견고한 테스트 작성
        assertThat(exception.getMessage()).isEqualTo("Article not found with id: " + nonExistentId);
    }
}