package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyPageResponseDto {
    private String nickname;
    private String profile;
    private String degree;
    private float grade;
    private String address;
    private String storeInfo;
    private List<ItemUserResponseDto> itemList;

    // 성훈 - 마이페이지 전체 조회
    public MyPageResponseDto(String nickname, String profile, String degree, float grade, String address, String storeInfo, List<ItemUserResponseDto> itemList) {
        this.nickname = nickname;
        this.profile = profile;
        this.degree = degree;
        this.grade = grade;
        this.address = address;
        this.storeInfo = storeInfo;
        this.itemList = itemList;
    }
}
