package com.nyx.Linkify.configuration.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@ControllerAdvice
public class BackendController {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of("message", "Required request body is missing"));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errorMesages = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errorMesages.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append(": ");
        });
        return ResponseEntity.badRequest().body(Map.of("message", errorMesages.toString()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleException(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String,String>> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,String>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        if(e.getMessage().contains("duplicate entry")){
            return ResponseEntity.badRequest().body(Map.of("message", "Email already exists, please use another email"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

}
