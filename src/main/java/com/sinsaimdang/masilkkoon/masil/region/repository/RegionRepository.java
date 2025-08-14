package com.sinsaimdang.masilkkoon.masil.region.repository;

import com.sinsaimdang.masilkkoon.masil.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByNameAndParent(String name, Region parent);

    Optional<Region> findByNameAndParentIsNull(String name);
}
