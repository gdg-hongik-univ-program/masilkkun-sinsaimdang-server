//package com.sinsaimdang.masilkkoon.masil.article.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Getter
//@Table(name = "regions")
//public class Region {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true, length = 50)
//    private String name; // 예: "경남", "서울"
//
//    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<SubRegion> subRegions = new ArrayList<>();
//}