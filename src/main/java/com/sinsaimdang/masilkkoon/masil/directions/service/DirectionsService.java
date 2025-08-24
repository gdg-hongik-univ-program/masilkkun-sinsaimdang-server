package com.sinsaimdang.masilkkoon.masil.directions.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectionsService {

    private final WebClient.Builder webClientBuilder;

    @Value("${kakao.mobility.api.key}")
    private String kakaoApiKey;

    private static final String KAKAO_DIRECTIONS_API_URL = "https://apis-navi.kakaomobility.com/v1/directions";

    public Mono<String> getDirections(String origin, String destination, String waypoints) {
        log.info("-> 카카오 길찾기 API 호출 시작 - origin: {}, destination: {}, waypoints: {}", origin, destination, waypoints);

        log.info("### 실제 사용되는 카카오 API 키: [KakaoAK {}] ###", kakaoApiKey);

        WebClient webClient = webClientBuilder.baseUrl(KAKAO_DIRECTIONS_API_URL).build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("origin", origin)
                        .queryParam("destination", destination)
                        .queryParam("waypoints", waypoints)
                        .queryParam("summary", "true")
                        .build())
                .header("Authorization", "KakaoAK " + kakaoApiKey) // "KA Header" 없이 Authorization 헤더만 사용합니다.
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("<- 카카오 길찾기 API 호출 성공"))
                .doOnError(error -> log.error("카카오 길찾기 API 호출 중 오류 발생", error));
    }
}