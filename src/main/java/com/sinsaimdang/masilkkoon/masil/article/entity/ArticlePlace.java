package com.sinsaimdang.masilkkoon.masil.article.entity;

import jakarta.persistence.Column; // JPA 컬럼 어노테이션
import jakarta.persistence.Embeddable; // 임베디드 타입 어노테이션
import lombok.Getter; // Lombok Getter
import lombok.Setter; // Lombok Setter (임베디드 타입도 Setter 지양 권장하나, 초기 편의상 포함)

@Embeddable // 이 클래스가 다른 엔티티에 포함될 수 있는 값 타입임을 명시
@Getter @Setter // Lombok Getter, Setter 자동 생성
public class ArticlePlace {

    @Column(nullable = false)
    private int placeOrder; // 장소 순서 (숫자순서)

    @Column(nullable = false, length = 255)
    private String placeName; // 장소 이름

    @Column(nullable = false, length = 500)
    private String address; // 장소 주소

    @Column(columnDefinition = "TEXT") // 장소 설명은 길 수 있으므로 TEXT 타입 권장
    private String description; // 장소 설명

    @Column(name = "photo_url", length = 1000)
    private String photoUrl;

    // JPA를 위한 기본 생성자 (protected 사용 권장)
    protected ArticlePlace() {
    }

    // 모든 필드를 받는 생성자 (객체 생성 시 초기화)
    public ArticlePlace(int placeOrder, String placeName, String address, String description, String photoUrl) {
        this.placeOrder = placeOrder;
        this.placeName = placeName;
        this.address = address;
        this.description = description;
        this.photoUrl = photoUrl;
    }
}