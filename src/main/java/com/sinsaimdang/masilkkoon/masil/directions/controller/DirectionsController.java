package com.sinsaimdang.masilkkoon.masil.directions.controller;

import com.sinsaimdang.masilkkoon.masil.common.util.ApiResponseUtil;
import com.sinsaimdang.masilkkoon.masil.directions.service.DirectionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/directions")
@RequiredArgsConstructor
@Slf4j
public class DirectionsController {

    private final DirectionsService directionsService;

    @GetMapping
    public Mono<ResponseEntity<Map<String, Object>>> getDirections(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(required = false) String waypoints) {

        log.info("API REQ >> GET /api/directions | origin: {}, destination: {}, waypoints: {}", origin, destination, waypoints);

        return directionsService.getDirections(origin, destination, waypoints)
                .map(responseBody -> {
                    log.info("API RES >> GET /api/directions | 길찾기 정보 조회 성공");
                    return ApiResponseUtil.success("길찾기 정보 조회 성공", responseBody);
                })
                .onErrorResume(e -> {
                    log.error("API RES >> GET /api/directions | 길찾기 정보 조회 중 오류 발생", e);
                    return Mono.just(ApiResponseUtil.internalServerError("길찾기 정보 조회 중 오류가 발생했습니다."));
                });
    }
}