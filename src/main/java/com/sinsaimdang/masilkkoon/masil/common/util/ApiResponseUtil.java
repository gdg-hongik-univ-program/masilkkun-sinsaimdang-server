package com.sinsaimdang.masilkkoon.masil.common.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ApiResponseUtil {

    public static ResponseEntity<Map<String, Object>> success(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<Map<String, Object>> success(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<Map<String, Object>> created(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public static ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<Map<String, Object>> badRequest(String message) {
        return error(HttpStatus.BAD_REQUEST, message);
    }

    public static ResponseEntity<Map<String, Object>> unauthorized(String message) {
        return error(HttpStatus.UNAUTHORIZED, message);
    }

    public static ResponseEntity<Map<String, Object>> internalServerError(String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
