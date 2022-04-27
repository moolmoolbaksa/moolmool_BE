package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyPageResponseDto {
    private String nickname;
    private String profile;
    private String grade;
    private String address;
    private String storeInfo;
    private List<ItemResponseDto> itemList;

    public MyPageResponseDto(String nickname, String profile, String grade, String address, String storeInfo, List<ItemResponseDto> itemList) {
        this.nickname = nickname;
        this.profile = profile;
        this.grade = grade;
        this.address = address;
        this.storeInfo = storeInfo;
        this.itemList = itemList;
    }
}
