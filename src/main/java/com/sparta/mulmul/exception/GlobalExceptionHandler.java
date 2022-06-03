package com.sparta.mulmul.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ResponseError> customExceptionHandler(CustomException e) {
        return ResponseError.createEntityFrom(e.getErrorCode());
    }
}