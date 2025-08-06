package com.sinsaimdang.masilkkoon.masil.article.dto;

import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleTag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
public class ArticleUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private String region;

    @NotNull
    private Set<ArticleTag> tags;

    @NotNull
    private List<PlaceInfo> places;

    @Getter
    @NoArgsConstructor
    public static class PlaceInfo {
        @NotNull
        private int placeOrder;
        @NotBlank
        private String placeName;
        @NotBlank
        private String address;
        @NotBlank
        private String description;
        @NotBlank
        private String photoUrl;
    }
}