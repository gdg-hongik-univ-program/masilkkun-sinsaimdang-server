//package com.sinsaimdang.masilkkoon.masil.article.entity;
//
//import com.sinsaimdang.masilkkoon.masil.article.entity.Article;
//import jakarta.persistence.*;
//import lombok.Getter;
//import java.util.List;
//
//@Entity
//@Getter
//@Table(name = "sub_regions")
//public class SubRegion {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, length = 50)
//    private String name; // 예: "부산", "창원", "강남구"
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "region_id")
//    private Region region; // 상위 지역 (그룹)
//
//    @OneToMany(mappedBy = "subRegion")
//    private List<Article> articles;
//}