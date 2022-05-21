package com.sparta.mulmul.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter @AllArgsConstructor
public enum ErrorCode {

    // USER
    NOT_FOUND_USER(HttpStatus.NOT_FOUND.value(), "U001", "해당 유저를 찾을 수 없습니다."),

    // FILE
    FILE_INVAILED(HttpStatus.BAD_REQUEST.value(), "F001", "잘못된 파일 형식입니다.");

    private final int httpStatus;
    private final String code;
    private final String message;
}
