package com.sinsaimdang.masilkkoon.masil.visit.repository;

import com.sinsaimdang.masilkkoon.masil.visit.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    Optional<Visit> findByUserIdAndRegionId(Long userId, Long regionId);

    @Modifying
    @Query("DELETE FROM Visit v WHERE v.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);


    List<Visit> findByUserIdAndRegionIdIn(Long userId, List<Long> regionIds);

    /**
     * 특정 사용자가 특정 지역을 방문한 기록이 있는지 확인하는 메서드
     * @param userId 사용자 ID
     * @param regionId 지역 ID
     * @return 방문 기록이 있으면 true, 없으면 false
     */
    boolean existsByUser_IdAndRegion_Id(Long userId, Long regionId);
}
