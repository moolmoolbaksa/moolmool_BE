package com.sparta.mulmul.user.userDto;

import com.sparta.mulmul.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCheckResponseDto {
    private String nickname;
    private String profile;
    private Long userId;

    public UserCheckResponseDto(User user){
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.profile = user.getProfile();
    }
}
