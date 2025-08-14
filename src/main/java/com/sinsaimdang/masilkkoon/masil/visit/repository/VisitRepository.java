package com.sinsaimdang.masilkkoon.masil.visit.repository;

import com.sinsaimdang.masilkkoon.masil.visit.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    Optional<Visit> findByUserIdAndRegionId(Long userId, Long regionId);
}
