package com.sinsaimdang.masilkkoon.masil.common.exception;

import com.sinsaimdang.masilkkoon.masil.common.util.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("잘못된 인자 예외 발생: {}", e.getMessage());
        return ApiResponseUtil.badRequest(e.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleSecurityException(SecurityException e) {
        log.warn("보안 정책 위반 예외 발생: {}", e.getMessage());
        return ApiResponseUtil.badRequest(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception e) {
        log.error("처리되지 않은 예외 발생", e);
        return ApiResponseUtil.internalServerError("서버 내부 오류가 발생했습니다.");
    }
}