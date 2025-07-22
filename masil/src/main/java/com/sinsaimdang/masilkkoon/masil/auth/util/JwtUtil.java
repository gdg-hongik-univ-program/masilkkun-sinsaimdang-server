package com.sinsaimdang.masilkkoon.masil.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

    // application.properties에 정의해 놓은 비밀키, 액세스 토큰 만료기한, 리프레시 토큰 만료기한, 발급자 정보를 @Value를 통해 주입한다
    // Spring의 @Value는 값 주입을 위해 사용하고,
    // Lombok의 @Value는 불변 클래스를 만들기 위해 사용된다. -> 필드를 private final로 만들고, 모든 필드를 포함하는 메서드를 자동으로 생성

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Value("${jwt.issuer}")
    private String issuer;

    // 서명에 사용할 키 생성
    // HMAC SHA-256 -> 입력 길이와 관계 없이 256bit 해시값을 생성하는 알고리즘
    // 변조감지, 데이터 무결성 보장, 비밀 키 로테이션 필요
    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    //Access Token 생성
    public String generateAccessToken(Long userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userID", userId);
        claims.put("userEmail", email);
        claims.put("userRole", role);
        return createToken(claims, email, accessTokenExpiration);
    }

    // Refresh Token 생성
    public String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email, refreshTokenExpiration);
    }

    // 공통 토큰 생성 메서드
    // Jwts.builder() : JWT 토큰 생성 메서드
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        return Jwts.builder()
                .setClaims(claims)                                                  // 사용자 정의 데이터 설정
                .setSubject(subject)                                                // 토큰 주체 : 일반적으로 사용자 이메일
                .setIssuer(issuer)                                                  // 토큰 발급자
                .setIssuedAt(new Date(System.currentTimeMillis()))                  // 토큰 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + expiration))   // 토큰 만료 시간
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)                // 서명 알고리즘
                .compact();                                                         // 최종 JWT 문자열 생성
    }

    // 토큰에서 사용자 ID 추출
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    // 토큰에서 사용자 email 추출
    public String extractUserEmail(String token) {
        return extractClaim(token, claims -> claims.get("userEmail", String.class));
    }

    // 토큰에서 사용자 권한 추출
    public String extractUserRole(String token) {
        return extractClaim(token, claims -> claims.get("userRole", String.class));
    }

    // 토큰 만료 시간 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()                 // 파서 빌더 생성
                    .setSigningKey(getSigningKey())     // 서명 검증용 키 설정
                    .build()                            // 파서 객체 생성
                    .parseClaimsJws(token)              // 토큰 파싱 + 검증
                    .getBody();                         // Claim 추출
        } catch (ExpiredJwtException e) {
            log.warn("만료된 토큰입니다: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 토큰입니다: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 토큰입니다: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.warn("JWT 토큰의 서명이 유효하지 않습니다: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("토큰이 비어 있습니다: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("JWT 토큰 처리 중 오류가 발생했습니다: {}", e.getMessage());
            throw e;
        }
    }
}
