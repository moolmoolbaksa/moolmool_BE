package com.sparta.mulmul.user.userDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserEditResponseDto {
    private boolean ok;

    private UserEditDtailResponseDto result;

    // 성훈 - 유저수정 반환값
    public UserEditResponseDto(Boolean ok, UserEditDtailResponseDto result) {
        this.ok = ok;
        this.result = result;
    }
}
