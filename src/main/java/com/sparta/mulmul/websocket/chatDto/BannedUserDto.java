package com.sparta.mulmul.websocket.chatDto;

import com.sparta.mulmul.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BannedUserDto {

    private Long userId;
    private String nickname;
    private String profile;

    public static BannedUserDto createFrom(User banndedUser){

        BannedUserDto dto = new BannedUserDto();
        dto.userId = banndedUser.getId();
        dto.nickname = banndedUser.getNickname();
        dto.profile = banndedUser.getProfile();

        return dto;
    }
}
