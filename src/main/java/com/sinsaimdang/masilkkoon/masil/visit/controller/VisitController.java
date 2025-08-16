package com.sinsaimdang.masilkkoon.masil.visit.controller;

import com.sinsaimdang.masilkkoon.masil.auth.dto.CurrentUser;
import com.sinsaimdang.masilkkoon.masil.common.util.ApiResponseUtil;
import com.sinsaimdang.masilkkoon.masil.visit.dto.VisitRequest;
import com.sinsaimdang.masilkkoon.masil.visit.service.VisitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
@Slf4j
public class VisitController {

    private final VisitService visitService;

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyLocation(
            @RequestBody VisitRequest visitRequest, CurrentUser currentUser) {

        log.info("Received VisitRequest DTO: {}", visitRequest.toString());
        log.info("API REQ >> POST /api/location/verify | 요청자 ID: {}", currentUser.getId());

        if(!currentUser.isAuthenticated()) {
            return ApiResponseUtil.unauthorized("로그인이 필요합니다");
        }

        boolean isVerified = visitService.verifyVisit(visitRequest.getRegion1DepthName(), visitRequest.getRegion2DepthName(), visitRequest.getFullAddressName(), currentUser.getId());

        if (isVerified) {
            log.info("API RES >> POST /api/location/verify | 위치 인증 및 기록 성공 | 요청자 ID: {}", currentUser.getId());
            return ApiResponseUtil.success("위치 인증에 성공했습니다.", Map.of("isVerified", true));
        } else {
            log.info("API RES >> POST /api/location/verify | 위치 인증 실패 | 요청자 ID: {}", currentUser.getId());
            return ApiResponseUtil.badRequest("인증할 수 없는 지역이거나, 잘못된 주소 정보입니다.");
        }
    }
}
