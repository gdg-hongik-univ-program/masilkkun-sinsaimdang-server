package com.sinsaimdang.masilkkoon.masil.article.service;

import com.sinsaimdang.masilkkoon.masil.article.entity.Article;
import com.sinsaimdang.masilkkoon.masil.article.repository.ArticleRepository;
import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleResponse;
import com.sinsaimdang.masilkkoon.masil.user.entity.UserRole;
import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleCreateRequest;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleUpdateRequest;
import com.sinsaimdang.masilkkoon.masil.article.dto.ArticleSearchCondition;
import com.sinsaimdang.masilkkoon.masil.region.entity.Region;
import com.sinsaimdang.masilkkoon.masil.region.repository.RegionRepository;
import com.sinsaimdang.masilkkoon.masil.visit.dto.VisitRequest;
import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleLike;
import com.sinsaimdang.masilkkoon.masil.article.repository.ArticleLikeRepository;
import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleScrap;
import com.sinsaimdang.masilkkoon.masil.article.repository.ArticleScrapRepository;
import com.sinsaimdang.masilkkoon.masil.article.entity.ArticlePlace;
import com.sinsaimdang.masilkkoon.masil.common.s3.Uploader;
import com.sinsaimdang.masilkkoon.masil.visit.service.VisitService;




import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Slf4j 임포트
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List; // 이 임포트는 더 이상 직접 사용되지 않지만, ArticleResponse의 반환 타입으로 사용될 수 있으므로 일단 유지
import java.util.Comparator;
import java.util.HashSet;
import java.util.Collections;
import java.util.Set; // Set 임포트 추가 (findAllArticles()의 내부 변환에서 사용)
import java.util.stream.Collectors; // Stream API를 위한 Collectors 임포트
import java.util.Map;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service // 스프링 빈으로 등록
@RequiredArgsConstructor // final 필드를 이용한 생성자 자동 생성 (의존성 주입)
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 설정 (조회 기능에 적합하며 성능 향상)
@Slf4j // Lombok을 이용한 로거 자동 생성
public class ArticleService {

    private final ArticleRepository articleRepository; // ArticleRepository 주입
    private final RegionRepository regionRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final UserRepository userRepository;
    private final ArticleScrapRepository articleScrapRepository;
    private final VisitService visitService;
    private final Uploader uploader;

    /**
     * 모든 게시글 목록 조회 (N+1 문제 해결을 위해 Fetch Join 적용)
     * @return 게시글 DTO 목록
     */
//    public List<ArticleResponse> findAllArticles() { // 메서드 반환 타입은 List 유지 (API 스펙)
//        // ArticleRepository의 findAllWithCollections() 메서드를 호출하여
//        // Article 엔티티와 연관된 컬렉션들을 Fetch Join으로 한 번에 가져옵니다.
//        List<Article> articles = articleRepository.findAllWithCollections();
//
//        // 조회된 Article 엔티티 리스트를 ArticleResponse DTO 리스트로 변환
//        // ArticleResponse의 생성자가 Set<ArticleTag>, Set<String>, Set<ArticlePlaceResponse>를 받으므로,
//        // 여기서 Collectors.toList()를 사용해도 문제는 없지만, Set의 특성(중복 없음)을 유지하려면 toSet()을 사용하는 것이 더 적절할 수 있습니다.
//        // API 반환 스펙이 List<ArticleResponse> 이므로, 최종적으로는 toList()가 맞습니다.
//        return articles.stream()
//                .map(ArticleResponse::new)
//                .collect(Collectors.toList()); // 최종 API 반환 스펙이 List이므로 toList() 유지
//    }

    /**
     * 특정 ID의 게시글 단건 조회 (N+1 문제 해결을 위해 Fetch Join 적용)
     * @param articleId 조회할 게시글 ID
     * @return 게시글 DTO
     * @throws IllegalArgumentException 해당 ID의 게시글을 찾을 수 없을 경우
     */
    @Transactional // 조회수 증가 로직 때문에 별도 트랜잭션 필요
    public ArticleResponse findArticleById(Long articleId, Long currentUserId) {
        log.info("-> 게시글 단건 조회 서비스 시작 - ID: {}, 역할: {}", articleId, currentUserId);

        // ArticleRepository의 findByIdWithCollections() 메서드를 호출하여
        // Article 엔티티와 연관된 컬렉션들을 Fetch Join으로 한 번에 가져옵니다.
        Article article = articleRepository.findByIdWithCollections(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id: " + articleId));
        log.debug("게시글 조회 성공 - ID: {}", articleId);

        // TODO: (나중에) 일반 사용자(USER)의 지역 인증 구현 시, 여기서 추가 권한 검증 로직 구현
        // 예시:
        // if (UserRole.USER.name().equals(userRole) && !article.getRegion().equals(인증된_유저의_지역)) {
        //     throw new AccessDeniedException("해당 지역의 게시글에 접근 권한이 없습니다."); // 적절한 예외 정의 필요
        // }

        // 조회수 증가 비즈니스 로직
        article.incrementViewCount(); // Article 엔티티 내부의 조회수 증가 메서드 호출
        log.debug("게시글 조회수 증가 완료 - ID: {}", articleId);

        boolean isLiked = false;
        boolean isScrapped = false;
        // 비로그인 상태(currentUserId=null)가 아닐 때만 DB를 조회합니다.
        if (currentUserId != null) {
            isLiked = articleLikeRepository.existsByUserIdAndArticleId(currentUserId, articleId);
            isScrapped = articleScrapRepository.existsByUserIdAndArticleId(currentUserId, articleId);
        }

        // 조회된 Article 엔티티를 ArticleResponse DTO로 변환
        log.info("<- 게시글 단건 조회 서비스 완료 - ID: {}", articleId);
        return new ArticleResponse(article, isLiked, isScrapped);
    }

    // TODO: 게시글 생성, 수정, 삭제 등의 비즈니스 로직은 나중에 추가
    /*
    @Transactional
    public ArticleResponse createArticle(ArticleCreateRequest request, User currentUser) {
        // ArticleCreateRequest로부터 데이터를 받아오고,
        // 현재 로그인한 사용자(currentUser) 정보를 사용하여 Article 엔티티를 생성합니다.
        Article article = new Article(
            request.getTitle(),
            request.getContent(),
            currentUser, // User 객체를 직접 전달
            request.getRegion(),
            new HashSet<>(request.getTags()),
            new HashSet<>(request.getPhotos()),
            new HashSet<>(request.getPlaces())
        );
        Article savedArticle = articleRepository.save(article);
        // 저장된 엔티티를 다시 DTO로 변환하여 반환
        return new ArticleResponse(savedArticle);
    }
    */

    /**
     * 게시글 목록 조회 및 필터링
     * @param condition 검색 조건 (지역, 태그)
     * @param pageable 페이징 정보
     * @return 필터링된 게시글 목록 (페이지네이션 포함)
     */
    @Transactional(readOnly = true)
    public Page<ArticleResponse> searchArticles(ArticleSearchCondition condition, Pageable pageable, Long currentUserId) {
        log.info("-> 게시글 목록 조회 서비스 시작 - 조건: {}, 역할: {}", condition, currentUserId);

        Page<Article> articlesPage = articleRepository.search(condition, pageable);

        // 비로그인 상태(currentUserId=null)일 경우, 모든 isLiked/isScrapped 값을 false로 설정
        if (currentUserId == null) {
            log.debug("비로그인 사용자 요청, 좋아요/스크랩 정보 없이 반환");
            return articlesPage.map(article -> new ArticleResponse(article, false, false));
        }

        // 로그인 상태일 경우, DB에서 '좋아요'/'스크랩' 정보를 한 번에 조회하여 처리
        List<Long> articleIds = articlesPage.getContent().stream()
                .map(Article::getId)
                .collect(Collectors.toList());

        Set<Long> likedArticleIds = Collections.emptySet();
        Set<Long> scrappedArticleIds = Collections.emptySet();

        if (!articleIds.isEmpty()) {
            likedArticleIds = articleLikeRepository.findLikedArticleIdsByUserIdAndArticleIds(currentUserId, articleIds);
            scrappedArticleIds = articleScrapRepository.findScrappedArticleIdsByUserIdAndArticleIds(currentUserId, articleIds);
        }

        final Set<Long> finalLikedIds = likedArticleIds;
        final Set<Long> finalScrappedIds = scrappedArticleIds;

        log.info("<- 게시글 목록 조회 서비스 완료");
        return articlesPage.map(article -> {
            boolean isLiked = finalLikedIds.contains(article.getId());
            boolean isScrapped = finalScrappedIds.contains(article.getId());
            return new ArticleResponse(article, isLiked, isScrapped);
        });    }

    @Transactional
    public void addLike(Long userId, Long articleId) {
        log.info("좋아요 처리 시작 - 사용자 ID :{}, 게시글 ID {}",userId, articleId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다" + userId));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다" + articleId));

        if (articleLikeRepository.existsByUserIdAndArticleId(userId, articleId)) {
            throw new IllegalArgumentException("이미 좋아요를 누른 게시글입니다.");
        }

        ArticleLike articleLike = new ArticleLike(user, article);
        articleLikeRepository.save(articleLike);

        article.incrementLikeCount();
        log.info("좋아요 처리 완료 - 사용자 ID :{}, 게시글 ID {}",userId, articleId);
    }

    @Transactional
    public void removeLike(Long userId, Long articleId) {
        log.info("좋아요 취소 처리 시작 - 사용자 ID :{}, 게시글 ID {}",userId, articleId);

        ArticleLike articleLike = articleLikeRepository.findByUserIdAndArticleId(userId, articleId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요를 누른 게시글이 아닙니다."));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        articleLikeRepository.delete(articleLike);

        article.decrementLikeCount();
        log.info("좋아요 취소 처리 완료 - 사용자 ID :{}, 게시글 ID {}",userId, articleId);
    }

    @Transactional
    public void addScrap(Long userId, Long articleId) {
        log.info("스크랩 처리 시작 - 사용자 ID :{}, 게시글 ID {}",userId, articleId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("스크랩 추가 실패 : 존재하지 않는 사용자 ID {}", userId);
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다" + userId);
                });

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> {
                    log.warn("스크랩 추가 실패 : 존재하지 않는 게시글 ID {}", articleId);
                    return new IllegalArgumentException("게시글을 찾을 수 없습니다" + articleId);
                });

        if (articleScrapRepository.existsByUserIdAndArticleId(userId, articleId)) {
            log.warn("스크랩 추가 실패 - 이미 스크랩한 게시글 : 사용자 ID {}, 게시글 ID {}", userId, articleId);
            throw new IllegalArgumentException("이미 스크랩한 게시글입니다.");
        }

        ArticleScrap articleScrap = new ArticleScrap(user, article);
        articleScrapRepository.save(articleScrap);

        article.incrementScrapCount();

        log.info("게시글 스크랩 추가 완료 - 사용자 ID:{}, 게시글 ID:{}", userId, articleId);
    }

    @Transactional
    public void removeScrap(Long userId, Long articleId) {
        log.info("게시글 스크랩 삭제 시작 - 사용자 ID: {}, 게시글 ID: {}", userId, articleId);

        ArticleScrap articleScrap = articleScrapRepository.findByUserIdAndArticleId(userId, articleId)
                .orElseThrow(() -> {
                    log.warn("스크랩 삭제 실패 - 스크랩하지 않은 게시글: 사용자 ID {}, 게시글 ID {}", userId, articleId);
                    return new IllegalArgumentException("스크랩하지 않은 게시글입니다.");
                });

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> {
                    log.warn("스크랩 삭제 실패 - 존재하지 않는 게시글: {}", articleId);
                    return new IllegalArgumentException("게시글을 찾을 수 없습니다: " + articleId);
                });

        articleScrapRepository.delete(articleScrap);
        article.decrementScrapCount();

        log.info("게시글 스크랩 삭제 완료 - 사용자 ID: {}, 게시글 ID: {}", userId, articleId);
    }

    @Transactional(readOnly = true)
    public Page<ArticleResponse> getScrapedArticles(Long userId, ArticleSearchCondition condition, Pageable pageable) {
        log.info("사용자 스크랩 목록 조회 시작 - 사용자 ID: {}, 검색 조건 {}, 페이지: {}", userId, condition, pageable.getPageNumber());

        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            log.warn("스크랩 목록 조회 실패 - 존재하지 않는 사용자: {}", userId);
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId);
        }

        Page<Article> scrapedArticles = articleRepository.searchScrapedArticles(userId, condition, pageable);

        List<Long> articleIds = scrapedArticles.getContent().stream()
                .map(Article::getId)
                .collect(Collectors.toList());

        Set<Long> likedArticleIds = articleIds.isEmpty() ? Collections.emptySet() :
                articleLikeRepository.findLikedArticleIdsByUserIdAndArticleIds(userId, articleIds);

        log.info("사용자 스크랩 목록 조회 완료 - 사용자 ID: {}, 조회된 게시글 수: {}",
                userId, scrapedArticles.getContent().size());

        return scrapedArticles.map(article -> {
            boolean isLiked = likedArticleIds.contains(article.getId());
            // 스크랩 목록 조회이므로 isScrapped는 항상 true입니다.
            return new ArticleResponse(article, isLiked, true);
        });
    }

    /**
     * 특정 사용자가 작성한 게시글 목록을 조회하는 메서드
     * @param userId 조회할 사용자 ID
     * @param pageable 페이징 정보
     * @return 해당 사용자가 작성한 게시글 목록 (페이지네이션 포함)
     */
    @Transactional(readOnly = true)
    public Page<ArticleResponse> findArticlesByUserId(Long userId, Long currentUserId, Pageable pageable) {
        log.info("-> 특정 사용자 작성 게시글 목록 조회 서비스 시작 - 사용자 ID: {}", userId);

        // Repository 메서드를 호출
        Page<Article> articles = articleRepository.findByUser_Id(userId, pageable);

        // 비로그인 상태(currentUserId=null)일 경우, false로 처리
        if (currentUserId == null) {
            return articles.map(article -> new ArticleResponse(article, false, false));
        }

        // 로그인 상태일 경우, 현재 로그인한 사용자를 기준으로 계산
        List<Long> articleIds = articles.getContent().stream().map(Article::getId).collect(Collectors.toList());
        Set<Long> likedArticleIds = articleIds.isEmpty() ? Collections.emptySet() :
                articleLikeRepository.findLikedArticleIdsByUserIdAndArticleIds(currentUserId, articleIds);
        Set<Long> scrappedArticleIds = articleIds.isEmpty() ? Collections.emptySet() :
                articleScrapRepository.findScrappedArticleIdsByUserIdAndArticleIds(currentUserId, articleIds);

        log.info("<- 특정 사용자 작성 게시글 목록 조회 서비스 완료 - 조회된 게시글 수: {}", articles.getContent().size());

        // 조회된 결과를 ArticleResponse DTO 페이지로 변환하여 반환
        return articles.map(article -> {
            boolean isLiked = likedArticleIds.contains(article.getId());
            boolean isScrapped = scrappedArticleIds.contains(article.getId());
            return new ArticleResponse(article, isLiked, isScrapped);
        });
    }

    /**
     * 게시글을 생성하는 메서드
     */
    @Transactional
    public ArticleResponse createArticle(ArticleCreateRequest request, List<MultipartFile> images, Long currentUserId) throws IOException {
        log.info("-> 게시글 생성 서비스 시작 - 작성자 ID: {}", currentUserId);

        // 1. 사용자 및 지역 정보 확인.
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + currentUserId));

        Region childRegion = findRegionFromCreateRequest(request.getPlaces());

        // 게시글 작성 전, 해당 지역 방문 여부 확인
        if (!visitService.hasUserVisitedRegion(currentUser.getId(), childRegion.getId())) {
            throw new IllegalArgumentException("해당 지역을 방문한 기록이 없어 게시글을 작성할 수 없습니다.");
        }

        // 모든 장소가 대표 지역과 일치하는지 검증
        boolean allPlacesAreInSameRegion = request.getPlaces().stream()
                .allMatch(place -> {
                    String placeRegionName = place.getRoadAddress().getRegion2DepthName(); // 각 장소의 '시/군/구' 이름
                    return childRegion.getName().equals(placeRegionName);
                });

        if (!allPlacesAreInSameRegion) {
            throw new IllegalArgumentException("모든 장소는 동일한 시/군/구에 속해야 합니다.");
        }


        // 2. 장소 및 이미지 유효성 검증

        // 장소 정보 리스트를 placeOrder 순으로 정렬합니다.
        List<ArticleCreateRequest.PlaceInfo> sortedPlaces = request.getPlaces().stream()
                .sorted(Comparator.comparingInt(ArticleCreateRequest.PlaceInfo::getPlaceOrder))
                .collect(Collectors.toList());

        // 장소의 개수와 이미지의 개수가 일치하는지 확인합니다.
        if (images == null || sortedPlaces.size() != images.size()) {
            log.warn("게시글 생성 실패: 장소 개수({})와 이미지 개수({}) 불일치", sortedPlaces.size(), images != null ? images.size() : 0);
            throw new IllegalArgumentException("장소의 개수와 이미지의 개수가 일치하지 않습니다.");
        }

        // 3. 장소 및 이미지 처리
        List<ArticlePlace> articlePlaces = new ArrayList<>();
        for (int i = 0; i < sortedPlaces.size(); i++) {
            ArticleCreateRequest.PlaceInfo placeInfo = sortedPlaces.get(i);
            MultipartFile imageFile = images.get(i); // 정렬된 순서에 맞는 이미지 파일

            // 각 이미지 파일이 비어있지 않은지 다시 한번 확인합니다.
            if (imageFile == null || imageFile.isEmpty()) {
                throw new IllegalArgumentException("업로드된 파일 중 비어있는 파일이 있습니다. (순서: " + (i + 1) + ")");
            }

            // 각 이미지를 S3(또는 로컬)에 업로드하고 URL을 받습니다.
            String photoUrl = uploader.upload(imageFile, "article-images");

            // 업로드된 URL을 포함하여 ArticlePlace 객체를 생성합니다.
            ArticlePlace articlePlace = new ArticlePlace(
                    placeInfo.getPlaceOrder(),
                    placeInfo.getPlaceName(),
                    placeInfo.getRoadAddress().getAddressName(),
                    placeInfo.getDescription(),
                    photoUrl
            );
            articlePlaces.add(articlePlace);
        }

        // 4. 게시글 엔티티 생성
        Article article = request.toEntity(currentUser, childRegion, articlePlaces);
        log.debug("Article 엔티티 생성 완료");

        // 5. Article 엔티티를 데이터베이스에 저장
        Article savedArticle = articleRepository.save(article);
        log.info("게시글 저장 완료 - ID: {}, 제목: {}", savedArticle.getId(), savedArticle.getTitle());

        return new ArticleResponse(savedArticle);
    }

    /**
     * 게시글을 삭제하는 메서드
     * @param articleId 삭제할 게시글 ID
     * @param currentUserId 현재 로그인한 사용자 ID
     */
    @Transactional
    public void deleteArticle(Long articleId, Long currentUserId) {
        log.info("게시글 삭제 서비스 호출 - 게시글 ID: {}, 요청자 ID: {}", articleId, currentUserId);

        // 1. articleId로 게시글을 DB에서 조회합니다. 없으면 예외를 발생시킵니다.
        Article article = articleRepository.findByIdWithCollections(articleId)
                .orElseThrow(() -> new IllegalArgumentException("ID " + articleId + "에 해당하는 게시글을 찾을 수 없습니다."));
        log.debug("게시글 조회 성공 - ID: {}", articleId);

        // 2.게시글의 작성자 ID와 현재 요청한 사용자의 ID가 일치하는지 확인합니다.
        if (!article.getUser().getId().equals(currentUserId)) {
            log.warn("게시글 삭제 권한 없음 - 게시글 작성자: {}, 요청자: {}", article.getUser().getId(), currentUserId);
            // 일치하지 않으면 SecurityException 예외를 발생시켜 삭제를 막습니다.
            throw new SecurityException("게시글을 삭제할 권한이 없습니다.");
        }

        // 3. 각 장소(ArticlePlace)에 포함된 모든 이미지 URL을 찾아서 S3에서 삭제합니다.
        for (ArticlePlace place : article.getArticlePlaces()) {
            if (place.getPhotoUrl() != null && !place.getPhotoUrl().isEmpty()) {
                uploader.delete(place.getPhotoUrl());
                log.info("S3 이미지 삭제: {}", place.getPhotoUrl());
            }
        }

        // 4. 연관된 '좋아요'와 '스크랩' 데이터 먼저 삭제
        articleLikeRepository.deleteAllByArticleId(articleId);
        articleScrapRepository.deleteAllByArticleId(articleId);

        // 5. 권한 확인이 통과되면 게시글을 삭제합니다.
        articleRepository.delete(article);
        log.info("게시글 삭제 완료 - ID: {}", articleId);
    }

    /**
     * 게시글을 수정하는 메서드
     * @param articleId 수정할 게시글 ID
     * @param request 수정할 내용이 담긴 DTO
     * @param newImages 새로 업로드되거나 교체될 이미지 파일 목록
     * @param currentUserId 현재 로그인한 사용자 ID
     * @return 수정된 게시글 정보를 담은 DTO
     */
    @Transactional
    public ArticleResponse updateArticle(Long articleId, ArticleUpdateRequest request, List<MultipartFile> newImages, Long currentUserId) throws IOException {
        log.info("게시글 수정 서비스 호출 - 게시글 ID: {}, 요청자 ID: {}", articleId, currentUserId);


        // 1. 게시글 조회 및 수정 권한 확인
        Article article = articleRepository.findByIdWithCollections(articleId)
                .orElseThrow(() -> new IllegalArgumentException("ID " + articleId + "에 해당하는 게시글을 찾을 수 없습니다."));

        if (!article.getUser().getId().equals(currentUserId)) {
            log.warn("게시글 수정 권한 없음 - 게시글 작성자: {}, 요청자: {}", article.getUser().getId(), currentUserId);
            throw new SecurityException("게시글을 수정할 권한이 없습니다.");
        }

        // 2. 삭제할 이미지 URL과 유지될 장소 목록 관리

        // 2-1. 기존 DB의 모든 이미지 URL을 "삭제 대상 후보"로 설정합니다.
        Set<String> urlsToDelete = article.getArticlePlaces().stream()
                .map(ArticlePlace::getPhotoUrl)
                .filter(url -> url != null && !url.isEmpty())
                .collect(Collectors.toSet());

        List<ArticlePlace> updatedArticlePlaces = new ArrayList<>();
        int newImageIndex = 0;

        // 2-2. 요청에 들어온 장소 목록을 순회하며 최종 장소 목록을 만듭니다.
        for (ArticleUpdateRequest.PlaceInfo placeInfo : request.getPlaces()) {
            String finalPhotoUrl = placeInfo.getPhotoUrl();

            // Case A: 사진을 '유지'하는 경우, 삭제 대상 후보에서 제거합니다.
            if (finalPhotoUrl != null && !finalPhotoUrl.isEmpty()) {
                urlsToDelete.remove(finalPhotoUrl);
            }
            // Case B: 사진을 '교체'하거나 '새로 추가'하는 경우
            else {
                // newImages 목록에서 순서대로 새 이미지를 꺼내 업로드합니다.
                if (newImages != null && newImageIndex < newImages.size()) {
                    finalPhotoUrl = uploader.upload(newImages.get(newImageIndex++), "article-images");
                } else {
                    finalPhotoUrl = null; // 이미지가 없는 경우
                }
            }
            // 최종 장소 목록에 추가합니다.
            updatedArticlePlaces.add(new ArticlePlace(
                    placeInfo.getPlaceOrder(),
                    placeInfo.getPlaceName(),
                    placeInfo.getRoadAddress().getAddressName(),
                    placeInfo.getDescription(),
                    finalPhotoUrl
            ));
        }

        // 3. 최종적으로 삭제 대상에 남은 URL들을 S3(또는 로컬)에서 삭제합니다.
        for (String imageUrlToDelete : urlsToDelete) {
            uploader.delete(imageUrlToDelete);
            log.info("S3에서 기존 이미지 삭제: {}", imageUrlToDelete);
        }

        // 4. 게시글 엔티티 업데이트
        Region childRegion = findRegionFromUpdateRequest(request.getPlaces());

        // 모든 장소가 대표 지역과 동일한 '시/군/구'에 속하는지 검증
        boolean allPlacesAreInSameRegion = request.getPlaces().stream()
                .allMatch(place -> {
                    String placeRegionName = place.getRoadAddress().getRegion2DepthName(); // 각 장소의 '시/군/구' 이름
                    return childRegion.getName().equals(placeRegionName);
                });

        if (!allPlacesAreInSameRegion) {
            throw new IllegalArgumentException("모든 장소는 동일한 시/군/구에 속해야 합니다.");
        }

        // Article 엔티티의 update 메서드를 호출하여 변경사항 적용
        article.update(request, childRegion, updatedArticlePlaces);

        // ▼▼▼ [핵심 수정] 응답을 반환하기 전, '좋아요'와 '스크랩' 상태를 다시 조회합니다. ▼▼▼
        boolean isLiked = false;
        boolean isScrapped = false;
        if (currentUserId != null) {
            isLiked = articleLikeRepository.existsByUserIdAndArticleId(currentUserId, articleId);
            isScrapped = articleScrapRepository.existsByUserIdAndArticleId(currentUserId, articleId);
        }

        log.info("<- 게시글 수정 서비스 완료 - ID: {}", articleId, isLiked, isScrapped);
        return new ArticleResponse(article, isLiked, isScrapped);
    }

    private Region findRegionFromCreateRequest(List<ArticleCreateRequest.PlaceInfo> places) {
        // 1. 장소 목록에서 첫 번째 장소를 찾습니다.
        ArticleCreateRequest.PlaceInfo firstPlace = places.stream()
                .min(Comparator.comparingInt(ArticleCreateRequest.PlaceInfo::getPlaceOrder)) // placeOrder가 1이 아니어도 가장 작은 순서의 장소를 찾음
                .orElseThrow(() -> new IllegalArgumentException("첫 번째 장소 정보가 반드시 필요합니다."));

        // 2. 첫 번째 장소의 roadAddress 객체를 가져옵니다.
        VisitRequest.RoadAddress address = firstPlace.getRoadAddress();
        if (address == null || address.getRegion1DepthName() == null || address.getRegion2DepthName() == null) {
            throw new IllegalArgumentException("첫 번째 장소의 주소 정보(roadAddress)가 올바르지 않습니다.");
        }

        // 3. 주소의 1depth 이름으로 부모 Region(예: 경기도)을 DB에서 찾습니다.
        Region parentRegion = regionRepository.findByNameAndParentIsNull(address.getRegion1DepthName())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상위 지역입니다: " + address.getRegion1DepthName()));

        // 4. 부모 Region과 2depth 이름으로 자식 Region(예: 수원시)을 DB에서 찾아서 반환합니다.
        return regionRepository.findByNameAndParent(address.getRegion2DepthName(), parentRegion)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 하위 지역입니다: " + address.getRegion2DepthName()));
    }

    private Region findRegionFromUpdateRequest(List<ArticleUpdateRequest.PlaceInfo> places) {
        // 1. 장소 목록에서 첫 번째 장소를 찾습니다.
        ArticleUpdateRequest.PlaceInfo firstPlace = places.stream()
                .min(Comparator.comparingInt(ArticleUpdateRequest.PlaceInfo::getPlaceOrder))
                .orElseThrow(() -> new IllegalArgumentException("첫 번째 장소 정보가 반드시 필요합니다."));

        // 2. 첫 번째 장소의 roadAddress 객체를 가져옵니다.
        VisitRequest.RoadAddress address = firstPlace.getRoadAddress();
        if (address == null || address.getRegion1DepthName() == null || address.getRegion2DepthName() == null) {
            throw new IllegalArgumentException("첫 번째 장소의 주소 정보(roadAddress)가 올바르지 않습니다.");
        }

        // 3. 주소의 1depth 이름으로 부모 Region(예: 경기도)을 DB에서 찾습니다.
        Region parentRegion = regionRepository.findByNameAndParentIsNull(address.getRegion1DepthName())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상위 지역입니다: " + address.getRegion1DepthName()));

        // 4. 부모 Region과 2depth 이름으로 자식 Region(예: 수원시)을 DB에서 찾아서 반환합니다.
        return regionRepository.findByNameAndParent(address.getRegion2DepthName(), parentRegion)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 하위 지역입니다: " + address.getRegion2DepthName()));
    }

}