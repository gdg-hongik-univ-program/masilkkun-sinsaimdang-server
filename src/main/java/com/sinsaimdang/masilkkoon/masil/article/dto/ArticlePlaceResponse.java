package com.sinsaimdang.masilkkoon.masil.article.dto;

import com.sinsaimdang.masilkkoon.masil.article.entity.ArticlePlace; // ArticlePlace 임포트
import lombok.EqualsAndHashCode; // Lombok EqualsAndHashCode 임포트
import lombok.Getter; // Lombok Getter
import lombok.Setter; // Lombok Setter

@Getter @Setter // Lombok Getter, Setter 자동 생성
@EqualsAndHashCode(of = {"placeOrder", "placeName", "address"}) // 내용 기반으로 동일 객체 판단
public class ArticlePlaceResponse {

    private int placeOrder; // 장소 순서
    private String placeName; // 장소 이름
    private String address; // 장소 주소
    private String description; // 장소 설명

    // ArticlePlace 엔티티를 ArticlePlaceResponse DTO로 변환하는 생성자
    public ArticlePlaceResponse(ArticlePlace articlePlace) {
        this.placeOrder = articlePlace.getPlaceOrder();
        this.placeName = articlePlace.getPlaceName();
        this.address = articlePlace.getAddress();
        this.description = articlePlace.getDescription();
    }

    // 기본 생성자 (필요시)
    public ArticlePlaceResponse() {
    }
}