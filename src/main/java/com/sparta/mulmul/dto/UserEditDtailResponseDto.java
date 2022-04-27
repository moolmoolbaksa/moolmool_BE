package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class UserEditDtailResponseDto {
    private String nickname;
    private String profile;
    private String address;
    private String storeInfo;

    public UserEditDtailResponseDto (String nickname, String profile, String address, String storeInfo) {
        this.nickname = nickname;
        this.profile = profile;
        this.address = address;
        this.storeInfo = storeInfo;
    }

}
