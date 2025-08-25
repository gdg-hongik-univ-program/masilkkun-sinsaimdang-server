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
}
