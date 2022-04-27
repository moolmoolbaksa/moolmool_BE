package com.sparta.mulmul.dto;

import com.sparta.mulmul.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCheckResponseDto {
    private String nickname;
    private String profile;

    public UserCheckResponseDto(User user){
        this.nickname = user.getNickname();
        this.profile = user.getProfile();
    }
}
