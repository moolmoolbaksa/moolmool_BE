package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class UserEditRequsetDto {
    private String nickname;
    private String profile;
    private String address;
//    private String favored;
    private String storeInfo;

    public UserEditRequsetDto(String nickname, String profile, String address, String storeInfo) {
        this.nickname = nickname;
        this.profile = profile;
        this.address = address;
//        this.favored = favored;
        this.storeInfo = storeInfo;

    }

}
