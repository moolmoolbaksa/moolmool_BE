package com.sparta.mulmul.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ResponseError {
    private final Boolean ok;
    private final int status;
    private final String code;
    private final String message;

    public static ResponseEntity<ResponseError> createFrom(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseError.builder()
                        .ok(false)
                        .status(errorCode.getHttpStatus())
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
                );
    }
}
