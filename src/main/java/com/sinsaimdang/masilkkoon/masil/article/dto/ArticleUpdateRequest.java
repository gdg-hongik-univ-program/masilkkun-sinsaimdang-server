package com.sinsaimdang.masilkkoon.masil.article.dto;

import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleTag;
import com.sinsaimdang.masilkkoon.masil.visit.dto.VisitRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ArticleUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private Set<ArticleTag> tags;

    @NotNull
    private List<PlaceInfo> places;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PlaceInfo {
        @NotNull
        private int placeOrder;

        @NotBlank
        private String placeName;

        @NotNull
        private VisitRequest.RoadAddress roadAddress;

        @NotBlank
        private String description;

        private String photoUrl;
    }
}