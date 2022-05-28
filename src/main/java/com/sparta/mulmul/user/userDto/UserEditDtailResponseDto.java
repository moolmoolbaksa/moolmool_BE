package com.sparta.mulmul.user.userDto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class UserEditDtailResponseDto {
    private String nickname;
    private String profile;
    private String address;
    private String storeInfo;

    // 성훈 - 유저 정보 수정
    public UserEditDtailResponseDto (String nickname, String profile, String address, String storeInfo) {
        this.nickname = nickname;
        this.profile = profile;
        this.address = address;
        this.storeInfo = storeInfo;
    }

}
