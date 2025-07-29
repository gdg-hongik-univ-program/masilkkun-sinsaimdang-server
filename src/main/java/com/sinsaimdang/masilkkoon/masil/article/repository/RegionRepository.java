//package com.sinsaimdang.masilkkoon.masil.article.repository;
//
//import com.sinsaimdang.masilkkoon.masil.region.entity.Region;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.util.Optional;
//
//public interface RegionRepository extends JpaRepository<Region, Long> {
//    @Query("SELECT r FROM Region r LEFT JOIN FETCH r.subRegions WHERE r.name = :name")
//    Optional<Region> findByNameWithSubRegions(String name);
//}