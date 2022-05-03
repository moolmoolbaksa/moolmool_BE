package com.sparta.mulmul.dto;


import lombok.Getter;

import java.util.List;

@Getter
public class UserStoreResponseDto {
    private String nickname;
    private String profile;
    private String degree;
    private float grade;
    private String address;
    private String storeInfo;
    private List<ItemUserResponseDto> itemList;

    // 이승재 / 유저 스토어 목록 보기
    public UserStoreResponseDto(String nickname, String profile, String degree, float grade, String address, String storeInfo, List<ItemUserResponseDto> itemList){
        this.nickname = nickname;
        this.profile = profile;
        this.degree = degree;
        this.grade = grade;
        this.address = address;
        this.storeInfo = storeInfo;
        this.itemList = itemList;
    }
}
