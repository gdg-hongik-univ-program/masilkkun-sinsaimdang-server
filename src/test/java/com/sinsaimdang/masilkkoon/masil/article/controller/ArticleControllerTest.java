package com.sinsaimdang.masilkkoon.masil.article.controller;

import com.sinsaimdang.masilkkoon.masil.article.entity.Article;
import com.sinsaimdang.masilkkoon.masil.article.repository.ArticleRepository;
import com.sinsaimdang.masilkkoon.masil.auth.util.JwtUtil;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.entity.UserRole;
import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
// @Transactional을 제거하고 @AfterEach로 데이터를 직접 관리합니다.
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    private String accessToken;
    private User testUser;
    private Article testArticle;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자(User)를 DB에 저장합니다.
        testUser = User.builder()
                .email("testuser@example.com")
                .password("password123")
                .name("테스트유저")
                .nickname("테스터")
                .role(UserRole.USER)
                .build();
        userRepository.save(testUser);

        // 테스트용 게시글(Article)을 DB에 저장합니다.
        testArticle = new Article("테스트 게시글", "내용입니다.", testUser, "서울", null, null, null);
        articleRepository.save(testArticle);

        // 저장된 사용자의 정보로 JWT 액세스 토큰을 생성합니다.
        accessToken = jwtUtil.generateAccessToken(
                testUser.getId(),
                testUser.getEmail(),
                testUser.getName(),
                testUser.getNickname(),
                testUser.getRole().name()
        );
    }

    @AfterEach
    void tearDown() {
        // 각 테스트가 끝난 후 DB를 직접 초기화합니다.
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    // =================== 단건 조회 테스트 ===================

    @Test
    @DisplayName("인증된 사용자의 게시글 단건 조회 성공")
    void getArticle_WithAuthenticatedUser_Success() throws Exception {
        Long articleId = testArticle.getId();

        mockMvc.perform(get("/api/articles/" + articleId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("테스트 게시글"))
                .andExpect(jsonPath("$.author.nickname").value("테스터"))
                .andDo(print());
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 게시글 단건 조회 실패 (401 에러)")
    void getArticle_WithUnauthenticatedUser_Fails() throws Exception {
        Long articleId = testArticle.getId();

        mockMvc.perform(get("/api/articles/" + articleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    // =================== 목록 조회 테스트 ===================

    @Test
    @DisplayName("인증된 사용자의 게시글 전체 목록 조회 성공")
    void getArticles_WithAuthenticatedUser_Success() throws Exception {
        mockMvc.perform(get("/api/articles")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // 수정된 부분: 응답 전체($)가 아닌, 응답 안의 content 필드($.content)가 배열인지 확인합니다.
                .andExpect(jsonPath("$.content").isArray())
                // 수정된 부분: content 배열의 첫 번째 요소($.content[0])의 title을 확인합니다.
                .andExpect(jsonPath("$.content[0].title").value("테스트 게시글"))
                .andDo(print());
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 게시글 전체 목록 조회 실패 (401 에러)")
    void getArticles_WithUnauthenticatedUser_Fails() throws Exception {
        mockMvc.perform(get("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}