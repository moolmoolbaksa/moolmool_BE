package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserEditResponseDto {
    private boolean ok;

    private UserEditDtailResponseDto result;

    public UserEditResponseDto(Boolean ok, UserEditDtailResponseDto result) {
        this.ok = ok;
        this.result = result;
    }
}
