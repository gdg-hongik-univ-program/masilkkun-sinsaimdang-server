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
     * @param userRole 현재 사용자의 역할 (인증되지 않은 경우 null)
     * @return 게시글 DTO
     * @throws IllegalArgumentException 해당 ID의 게시글을 찾을 수 없을 경우
     */
    @Transactional // 조회수 증가 로직 때문에 별도 트랜잭션 필요
    public ArticleResponse findArticleById(Long articleId, String userRole) {
        log.info("-> 게시글 단건 조회 서비스 시작 - ID: {}, 역할: {}", articleId, userRole);

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

        // 조회된 Article 엔티티를 ArticleResponse DTO로 변환
        log.info("<- 게시글 단건 조회 서비스 완료 - ID: {}", articleId);
        return new ArticleResponse(article);
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
     * @param userRole 현재 사용자의 역할 (인증되지 않은 경우 null)
     * @return 필터링된 게시글 목록 (페이지네이션 포함)
     */
    @Transactional(readOnly = true)
    public Page<ArticleResponse> searchArticles(ArticleSearchCondition condition, Pageable pageable, String userRole) {
        log.info("-> 게시글 목록 조회 서비스 시작 - 조건: {}, 역할: {}", condition, userRole);

        // 관리자(ADMIN) 역할일 경우 지역 필터링을 무시합니다.
        // 'ADMIN'은 UserRole enum의 문자열 이름이므로, equals로 비교합니다.
        if (UserRole.ADMIN.name().equals(userRole)) { // 관리자 역할 분기 처리
            // condition.setRegion(null)을 통해 Querydsl의 regionEq 조건이 적용되지 않도록 합니다.
            // (regionEq는 StringUtils.hasText(region) 검사를 하므로 null이면 조건이 적용되지 않음)
            condition.setRegion(null);
            log.debug("ADMIN 역할이므로 지역 필터링 조건을 초기화합니다."); // 로깅
        }
        // TODO: (나중에) 일반 사용자(USER)의 지역 인증 로직 구현 시,
        // 이곳에서 `condition.getRegion()`과 사용자의 "인증된 지역"을 비교하는 로직을 추가합니다.
        // 만약 USER인데 region이 null이라면, 사용자의 인증된 지역을 기본으로 설정하거나,
        // 지역 미선택 시 기본 동작(예: 모든 지역 조회 허용 또는 오류 반환)을 정의해야 합니다.

        log.info("<- 게시글 목록 조회 서비스 완료");
        return articleRepository.search(condition, pageable).map(ArticleResponse::new);
    }

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

        log.info("게시글 스크랩 추가 완료 - 사용자 ID:{}, 게시글 ID:{}");
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
    public Page<ArticleResponse> getScrapedArticles(Long userId, Pageable pageable) {
        log.info("사용자 스크랩 목록 조회 시작 - 사용자 ID: {}, 페이지: {}", userId, pageable.getPageNumber());

        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            log.warn("스크랩 목록 조회 실패 - 존재하지 않는 사용자: {}", userId);
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId);
        }

        Page<Article> scrapedArticles = articleScrapRepository.findScrapedArticlesByUserId(userId, pageable);

        log.info("사용자 스크랩 목록 조회 완료 - 사용자 ID: {}, 조회된 게시글 수: {}",
                userId, scrapedArticles.getContent().size());

        return scrapedArticles.map(ArticleResponse::new);
    }

    /**
     * 특정 사용자가 작성한 게시글 목록을 조회하는 메서드
     * @param userId 조회할 사용자 ID
     * @param pageable 페이징 정보
     * @return 해당 사용자가 작성한 게시글 목록 (페이지네이션 포함)
     */
    @Transactional(readOnly = true)
    public Page<ArticleResponse> findArticlesByUserId(Long userId, Pageable pageable) {
        log.info("-> 특정 사용자 작성 게시글 목록 조회 서비스 시작 - 사용자 ID: {}", userId);

        // Repository 메서드를 호출
        Page<Article> articles = articleRepository.findByUser_Id(userId, pageable);

        log.info("<- 특정 사용자 작성 게시글 목록 조회 서비스 완료 - 조회된 게시글 수: {}", articles.getContent().size());

        // 조회된 결과를 ArticleResponse DTO 페이지로 변환하여 반환
        return articles.map(ArticleResponse::new);
    }

    /**
     * 게시글을 생성하는 메서드
     */
    @Transactional
    public ArticleResponse createArticle(ArticleCreateRequest request, List<MultipartFile> images, Long currentUserId) throws IOException {
        log.info("-> 게시글 생성 서비스 시작 - 작성자 ID: {}", currentUserId);

        // Controller에서 User 객체를 받아오는 대신, Service에서 직접 조회합니다.
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + currentUserId));

        // private 메소드를 호출하여 요청 데이터로부터 Region 엔티티를 찾아옵니다.
        Region childRegion = findRegionFromRequest(request.getPlaces());

        // 모든 장소가 대표 지역과 일치하는지 검증
        boolean allPlacesAreInSameRegion = request.getPlaces().stream()
                .allMatch(place -> {
                    String placeRegionName = place.getRoadAddress().getRegion2DepthName(); // 각 장소의 '시/군/구' 이름
                    return childRegion.getName().equals(placeRegionName);
                });

        if (!allPlacesAreInSameRegion) {
            throw new IllegalArgumentException("모든 장소는 동일한 시/군/구에 속해야 합니다.");
        }

        // 게시글 작성 전, 해당 지역 방문 여부 확인
        if (!visitService.hasUserVisitedRegion(currentUser.getId(), childRegion.getId())) {
            throw new IllegalArgumentException("해당 지역을 방문한 기록이 없어 게시글을 작성할 수 없습니다.");
        }

        // 장소 정보 리스트를 placeOrder 순으로 정렬합니다.
        List<ArticleCreateRequest.PlaceInfo> sortedPlaces = request.getPlaces().stream()
                .sorted(Comparator.comparingInt(ArticleCreateRequest.PlaceInfo::getPlaceOrder))
                .collect(Collectors.toList());

        List<ArticlePlace> articlePlaces = new ArrayList<>();
        for (int i = 0; i < sortedPlaces.size(); i++) {
            ArticleCreateRequest.PlaceInfo placeInfo = sortedPlaces.get(i);
            MultipartFile imageFile = images.get(i); // 정렬된 순서에 맞는 이미지 파일

        // 각 이미지를 S3(또는 로컬)에 업로드하고 URL을 받습니다.
        String photoUrl = uploader.upload(imageFile, "article-images");

        // 업로드된 URL을 포함하여 ArticlePlace 객체를 생성합니다.
        ArticlePlace articlePlace = new ArticlePlace(
                placeInfo.getPlaceOrder(),
                placeInfo.getPlaceName(),
                placeInfo.getRoadAddress().getAddressName(),
                placeInfo.getDescription(),
                photoUrl // 업로드된 실제 이미지 URL
        );
        articlePlaces.add(articlePlace);
    }

        // 3. DTO를 Article 엔티티로 변환할 때, 찾아낸 Region 객체를 함께 전달합니다.
        Article article = request.toEntity(currentUser, childRegion, articlePlaces);
        log.debug("Article 엔티티 생성 완료");

        // 4. Article 엔티티를 데이터베이스에 저장합니다.
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

        // 2. (핵심) 게시글의 작성자 ID와 현재 요청한 사용자의 ID가 일치하는지 확인합니다.
        if (!article.getUser().getId().equals(currentUserId)) {
            log.warn("게시글 삭제 권한 없음 - 게시글 작성자: {}, 요청자: {}", article.getUser().getId(), currentUserId);
            // 일치하지 않으면 SecurityException 예외를 발생시켜 삭제를 막습니다.
            throw new SecurityException("게시글을 삭제할 권한이 없습니다.");
        }

        // 각 장소(ArticlePlace)에 포함된 모든 이미지 URL을 찾아서 S3에서 삭제합니다.
        for (ArticlePlace place : article.getArticlePlaces()) {
            if (place.getPhotoUrl() != null && !place.getPhotoUrl().isEmpty()) {
                uploader.delete(place.getPhotoUrl());
                log.info("S3 이미지 삭제: {}", place.getPhotoUrl());
            }
        }

        // 3. 권한 확인이 통과되면 게시글을 삭제합니다.
        articleRepository.delete(article);
        log.info("게시글 삭제 완료 - ID: {}", articleId);
    }

    /**
     * 게시글을 수정하는 메서드
     * @param articleId 수정할 게시글 ID
     * @param request 수정할 내용이 담긴 DTO
     * @param currentUserId 현재 로그인한 사용자 ID
     * @return 수정된 게시글 정보를 담은 DTO
     */
    @Transactional
    public ArticleResponse updateArticle(Long articleId, ArticleUpdateRequest request, List<MultipartFile> newImages, Long currentUserId) throws IOException {
        log.info("게시글 수정 서비스 호출 - 게시글 ID: {}, 요청자 ID: {}", articleId, currentUserId);

        Article article = articleRepository.findByIdWithCollections(articleId)
                .orElseThrow(() -> new IllegalArgumentException("ID " + articleId + "에 해당하는 게시글을 찾을 수 없습니다."));

        if (!article.getUser().getId().equals(currentUserId)) {
            log.warn("게시글 수정 권한 없음 - 게시글 작성자: {}, 요청자: {}", article.getUser().getId(), currentUserId);
            throw new SecurityException("게시글을 수정할 권한이 없습니다.");
        }

//        // 1. 기존 장소(ArticlePlace)들을 Map 형태로 바꿔서 쉽게 찾을 수 있도록 준비합니다.
//        Map<Integer, ArticlePlace> oldPlacesMap = article.getArticlePlaces().stream()
//                .collect(Collectors.toMap(ArticlePlace::getPlaceOrder, Function.identity()));
//
//        // 2. 요청으로 들어온 새로운 장소 정보 목록을 placeOrder 순으로 정렬합니다.
//        List<ArticleUpdateRequest.PlaceInfo> newPlaceInfos = request.getPlaces().stream()
//                .sorted(Comparator.comparingInt(ArticleUpdateRequest.PlaceInfo::getPlaceOrder))
//                .collect(Collectors.toList());
//
//        List<ArticlePlace> updatedArticlePlaces = new ArrayList<>();
//        int newImageIndex = 0;
//
//        // 3. 요청으로 들어온 장소 목록을 하나씩 확인하며 업데이트/추가 작업을 수행합니다.
//        for (ArticleUpdateRequest.PlaceInfo placeInfo : newPlaceInfos) {
//            String photoUrl;
//            // Case A: 기존에 있던 장소 (placeOrder가 동일)
//            if (oldPlacesMap.containsKey(placeInfo.getPlaceOrder())) {
//                ArticlePlace oldPlace = oldPlacesMap.get(placeInfo.getPlaceOrder());
//                // Case A-1: 사진이 변경되지 않고 유지됨 (remainingPhotoUrls에 포함)
//                if (request.getRemainingPhotoUrls() != null && request.getRemainingPhotoUrls().contains(oldPlace.getPhotoUrl())) {
//                    photoUrl = oldPlace.getPhotoUrl(); // 기존 URL 사용
//                }
//                // Case A-2: 사진이 변경됨 (새 이미지 파일로 교체)
//                else {
//                    uploader.delete(oldPlace.getPhotoUrl()); // 기존 이미지 삭제
//                    photoUrl = uploader.upload(newImages.get(newImageIndex++), "article-images"); // 새 이미지 업로드
//                }
//            }
//            // Case B: 새로 추가된 장소
//            else {
//                photoUrl = uploader.upload(newImages.get(newImageIndex++), "article-images"); // 새 이미지 업로드
//            }
//
//            ArticlePlace updatedPlace = new ArticlePlace(
//                    placeInfo.getPlaceOrder(),
//                    placeInfo.getPlaceName(),
//                    placeInfo.getRoadAddress().getAddressName(),
//                    placeInfo.getDescription(),
//                    photoUrl
//            );
//            updatedArticlePlaces.add(updatedPlace);
//        }
//
//        // 4. 삭제된 장소를 찾아서 S3의 이미지를 삭제합니다.
//        Set<Integer> newPlaceOrders = newPlaceInfos.stream()
//                .map(ArticleCreateRequest.PlaceInfo::getPlaceOrder)
//                .collect(Collectors.toSet());
//
//        for (ArticlePlace oldPlace : article.getArticlePlaces()) {
//            if (!newPlaceOrders.contains(oldPlace.getPlaceOrder())) {
//                uploader.delete(oldPlace.getPhotoUrl());
//            }
//        }
//        // 수정 요청에서도 동일하게 Region 엔티티를 찾아옵니다.
//        Region childRegion = findRegionFromRequest(request.getPlaces());
//
//        // Article 엔티티의 update 메소드를 호출할 때, 찾아낸 Region 객체를 함께 전달합니다.
//        article.update(request, childRegion, updatedArticlePlaces);
//        log.debug("게시글 내용 업데이트 완료 (Dirty Checking 대상) - ID: {}", articleId);
//
//        log.info("<- 게시글 수정 서비스 완료 - ID: {}", articleId);
//        return new ArticleResponse(article);
//    }

        // --- 이미지 및 장소 정보 처리 ---

        // 1. 기존 이미지 URL들을 Set으로 만들어 쉽게 검색할 수 있도록 준비합니다.
        Set<String> oldImageUrls = article.getArticlePlaces().stream()
                .map(ArticlePlace::getPhotoUrl)
                .filter(url -> url != null && !url.isEmpty())
                .collect(Collectors.toSet());

        // 2. 프론트에서 "유지하겠다"고 보낸 이미지 URL 목록입니다.
        Set<String> remainingImageUrls = new HashSet<>(request.getRemainingPhotoUrls() != null ? request.getRemainingPhotoUrls() : List.of());

        // 3. DB에 최종적으로 저장될 새로운 장소 목록을 담을 리스트입니다.
        List<ArticlePlace> updatedArticlePlaces = new ArrayList<>();
        int newImageIndex = 0; // 새로 업로드된 이미지 파일의 인덱스

        // 4. 요청으로 들어온 장소 목록(수정 후의 최종 목록)을 순회합니다.
        for (ArticleUpdateRequest.PlaceInfo placeInfo : request.getPlaces()) {
            String finalPhotoUrl = null;

            // 프론트엔드에서 유지할 사진의 URL을 직접 보내주는 것이 가장 이상적이지만,
            // 현재 DTO 구조상 placeInfo에 기존 URL이 없으므로, 기존 장소 목록에서 찾아 매칭합니다.
            String existingUrlToKeep = findMatchingUrl(placeInfo, article.getArticlePlaces(), remainingImageUrls);

            // Case A: 이 장소의 사진을 '유지'하는 경우
            if (existingUrlToKeep != null) {
                finalPhotoUrl = existingUrlToKeep;
            }
            // Case B: 사진을 '교체'하거나 '새로 추가'하는 경우
            else if (newImages != null && newImageIndex < newImages.size()) {
                finalPhotoUrl = uploader.upload(newImages.get(newImageIndex++), "article-images");
            }

            updatedArticlePlaces.add(new ArticlePlace(
                    placeInfo.getPlaceOrder(),
                    placeInfo.getPlaceName(),
                    placeInfo.getRoadAddress().getAddressName(),
                    placeInfo.getDescription(),
                    finalPhotoUrl
            ));
        }

        // 5. 삭제되어야 할 이미지들을 S3에서 실제로 제거합니다. (기존 이미지 전체 - 유지할 이미지)
        oldImageUrls.removeAll(remainingImageUrls);
        for (String imageUrlToDelete : oldImageUrls) {
            uploader.delete(imageUrlToDelete);
        }

        // 6. Article 엔티티를 업데이트합니다.
        Region childRegion = findRegionFromRequest(request.getPlaces());

        // 모든 장소가 대표 지역과 일치하는지 검증
        boolean allPlacesAreInSameRegion = request.getPlaces().stream()
                .allMatch(place -> {
                    String placeRegionName = place.getRoadAddress().getRegion2DepthName(); // 각 장소의 '시/군/구' 이름
                    return childRegion.getName().equals(placeRegionName);
                });

        if (!allPlacesAreInSameRegion) {
            throw new IllegalArgumentException("모든 장소는 동일한 시/군/구에 속해야 합니다.");
        }

        article.update(request, childRegion, updatedArticlePlaces); // Article 엔티티의 update 메서드 호출

        log.info("<- 게시글 수정 서비스 완료 - ID: {}", articleId);
        return new ArticleResponse(article);
    }

    // updateArticle 내부에서 사용할 헬퍼 메서드
    private String findMatchingUrl(ArticleUpdateRequest.PlaceInfo newPlace, List<ArticlePlace> oldPlaces, Set<String> remainingUrls) {
        // 기존 장소 목록에서 이름과 주소가 같은 것을 찾아
        return oldPlaces.stream()
                .filter(oldPlace -> oldPlace.getPlaceName().equals(newPlace.getPlaceName()) && oldPlace.getAddress().equals(newPlace.getRoadAddress().getAddressName()))
                .map(ArticlePlace::getPhotoUrl) // 그 장소의 기존 URL을 가져온 뒤
                .filter(remainingUrls::contains) // 그 URL이 "유지할 목록"에 있는지 확인
                .findFirst()
                .orElse(null);
    }

    // 요청 DTO에서 Region을 찾는 중복 로직 추출
    // ArticleCreateRequest와 ArticleUpdateRequest가 동일한 구조의 PlaceInfo를 가지고 있어 제네릭(<T>)으로 처리
    private Region findRegionFromRequest(List<? extends ArticleCreateRequest.PlaceInfo> places) {
        // 1. 장소 목록에서 첫 번째 장소를 찾습니다.
        ArticleCreateRequest.PlaceInfo firstPlace = places.stream()
//                .filter(p -> p.getPlaceOrder() == 1)
//                .findFirst()
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

}
