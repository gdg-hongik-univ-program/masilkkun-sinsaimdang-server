package com.sinsaimdang.masilkkoon.masil.article.dto;

import com.sinsaimdang.masilkkoon.masil.article.entity.Article;
import com.sinsaimdang.masilkkoon.masil.article.entity.ArticlePlace;
import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleTag;
import com.sinsaimdang.masilkkoon.masil.region.entity.Region;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.visit.dto.VisitRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@ToString
public class ArticleCreateRequest {

    @NotBlank
    private String title;

//    @NotBlank
    private String content;

    @NotNull
    private Set<ArticleTag> tags;

    @NotNull
    private List<PlaceInfo> places;

    public Article toEntity(User user, Region region) {
        // places 리스트를 ArticlePlace 엔티티 Set으로 변환하는 로직 추가
        Set<ArticlePlace> articlePlaces = this.places.stream()
                .map(placeInfo -> new ArticlePlace(
                        placeInfo.getPlaceOrder(),
                        placeInfo.getPlaceName(),
                        placeInfo.getRoadAddress().getAddressName(),
                        placeInfo.getDescription()))
                .collect(Collectors.toSet());

        // photos 리스트에서 URL만 추출하는 로직 추가
        List<String> photoUrls = this.places.stream()
                .map(PlaceInfo::getPhotoUrl)
                .collect(Collectors.toList());

        // Article 엔티티 생성자에 맞게 수정
        return new Article(
                this.title,
                this.content,
                user,
                region,
                this.tags,
                photoUrls,
                articlePlaces
        );
    }

    @Getter
    @NoArgsConstructor
    public static class PlaceInfo {
        @NotNull // 장소 순서 필수
        private int placeOrder;
        @NotBlank
        private String placeName;
        @NotNull // roadAddress 필수
        private VisitRequest.RoadAddress roadAddress;
        @NotBlank
        private String description;
        @NotBlank
        private String photoUrl;
    }
}