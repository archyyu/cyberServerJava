package com.cybercafe.controller;

import com.cybercafe.model.exception.SurfException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SurfException.class)
    public ResponseEntity<Map<String, String>> handleSurfException(SurfException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
}
