package com.sinsaimdang.masilkkoon.masil.article.repository;

import com.sinsaimdang.masilkkoon.masil.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository; // @Repository 어노테이션은 생략 가능하지만, 명시적으로 유지
import java.util.List;
import java.util.Optional;

@Repository // 스프링 빈으로 등록 (생략 가능하지만, 명시적으로 유지)
public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom {

    // N+1 문제를 해결하기 위해 Fetch Join을 사용하여 모든 게시글과 연관된 컬렉션들을 한 번에 가져옵니다.
    // SELECT DISTINCT a: 여러 컬렉션을 Fetch Join 할 때 발생할 수 있는 중복 엔티티를 메모리에서 제거합니다.
    @Query("SELECT DISTINCT a FROM Article a " +
            "LEFT JOIN FETCH a.user u " +
            "LEFT JOIN FETCH a.articleTags " +
            "LEFT JOIN FETCH a.photos " +
            "LEFT JOIN FETCH a.articlePlaces")
    List<Article> findAllWithCollections();

    // 단건 조회 시에도 N+1 문제를 방지하고 중복을 제거하기 위해 Fetch Join과 DISTINCT를 적용합니다.
    @Query("SELECT DISTINCT a FROM Article a " +
            "LEFT JOIN FETCH a.user u " +
            "LEFT JOIN FETCH a.articleTags " +
            "LEFT JOIN FETCH a.photos " +
            "LEFT JOIN FETCH a.articlePlaces " +
            "WHERE a.id = :id")
    Optional<Article> findByIdWithCollections(Long id);
}