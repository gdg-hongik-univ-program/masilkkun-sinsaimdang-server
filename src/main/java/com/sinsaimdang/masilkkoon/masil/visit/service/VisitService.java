package com.sinsaimdang.masilkkoon.masil.visit.service;

import com.sinsaimdang.masilkkoon.masil.visit.dto.MapStampResponse;
import com.sinsaimdang.masilkkoon.masil.visit.entity.Visit;
import com.sinsaimdang.masilkkoon.masil.visit.repository.VisitRepository;
import com.sinsaimdang.masilkkoon.masil.region.entity.Region;
import com.sinsaimdang.masilkkoon.masil.region.repository.RegionRepository;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VisitService {

    private final RegionRepository regionRepository;
    private final VisitRepository userVisitRepository;
    private final UserRepository userRepository;

    public boolean verifyVisit(String region1DepthName, String region2DepthName, String fullAddressName, Long userId) {
        log.info("지역 방문 인증 시작 - 사용자 ID: {}, 요청 주소: {}", userId, fullAddressName);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    return new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId);
                });

        if (region1DepthName == null || region1DepthName.trim().isEmpty() ||
                region2DepthName == null || region2DepthName.trim().isEmpty()) {
            log.warn("인증 실패: 주소 정보가 누락되었습니다. (요청 주소: {})", fullAddressName);
            return false;
        }

        Region parentRegion = regionRepository.findByNameAndParentIsNull(region1DepthName)
                .orElse(null);

        if (parentRegion == null) {
            log.warn("인증 실패: 유효하지 않은 시/도입니다. (요청 주소: {})", fullAddressName);
            return false;
        }

        Region childRegion = regionRepository.findByNameAndParent(region2DepthName, parentRegion)
                .orElse(null);

        if (childRegion == null) {
            log.warn("인증 실패: 유효하지 않은 시/군/구이거나, 올바른 소속 관계가 아닙니다. (요청 주소: {})", fullAddressName);
            return false;
        }

        recordVisit(user, childRegion);
        recordVisit(user, parentRegion);

        log.info("지역 방문 인증 및 기록 완료 - 사용자 ID: {}, 등록 지역: {}/{}", userId, parentRegion.getName(), childRegion.getName());
        return true;
    }

    private void recordVisit(User user, Region region) {
        Visit visit = userVisitRepository.findByUserIdAndRegionId(user.getId(), region.getId())
                        .orElse(null);

        if (visit == null) {
            log.debug("첫 방문 기록 생성 - 사용자 ID :{}, 지역 :{}", user.getId(), region.getName());
            visit = new Visit(user, region);
        } else {
            log.debug("방문횟수 증가 - 사용자 ID {}, 지역 {}, 기존 방문 {}, 업데이트 횟수 {}", user.getId(), region.getName(), visit.getVisitCount(), visit.getVisitCount() + 1);
            visit.incrementVisitCount();
        }
        userVisitRepository.save(visit);
    }

    @Transactional
    public List<MapStampResponse> getMapStamps(Long userId) {
        log.info("스탬프 지도 정보 조회 시작 - 사용자 ID {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    return new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId);
                });

        List<Region> allTopRegions = regionRepository.findAllByParentIsNull();
        List<MapStampResponse> response = new ArrayList<>();

        for (Region topRegion : allTopRegions) {
            List<Region> children  = topRegion.getChildren();
            int totalChildrenCount = children.size();

            if(totalChildrenCount == 0){
                boolean visited = userVisitRepository.existsByUser_IdAndRegion_Id(userId, topRegion.getId());
                int colorLevel = visited ? 5 : 0;
                response.add(new MapStampResponse(topRegion.getName(), colorLevel));
                continue;
            }

            List<Long> childrenIds = children.stream()
                    .map(Region::getId)
                    .collect(Collectors.toList());

            long visitedChildrenCount = userVisitRepository.countByUserIdAndRegionIdIn(userId, childrenIds);

            double visitedRatio = (double) visitedChildrenCount / (double) totalChildrenCount;
            int colorLevel = calculateColorLevel(visitedRatio);

            response.add(new MapStampResponse(topRegion.getName(), colorLevel));
        }
        log.info("스탬프 지도 정보 조회 완료 - 사용자 ID {}", userId);
        return response;
    }

    private int calculateColorLevel(double ratio) {
        if (ratio >= 1.0) return 4;
        if (ratio >= 0.66  ) return 3;
        if (ratio >= 0.33) return 2;
        if (ratio > 0) return 1;
        return 0;
    }

    /**
     * 사용자가 특정 지역을 방문했는지 확인하는 서비스 메서드
     * @param userId 확인할 사용자 ID
     * @param regionId 확인할 지역 ID
     * @return 방문 기록 존재 여부 (true/false)
     */
    @Transactional(readOnly = true)
    public boolean hasUserVisitedRegion(Long userId, Long regionId) {
        return userVisitRepository.existsByUser_IdAndRegion_Id(userId, regionId);
    }
}
