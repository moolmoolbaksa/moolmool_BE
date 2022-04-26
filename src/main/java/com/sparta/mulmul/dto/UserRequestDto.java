package com.sparta.mulmul.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {
    private String username;
    private String nickname;
    private String password;
    private String passwordCheck;
}