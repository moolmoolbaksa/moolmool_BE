package com.sparta.mulmul.user.userDto;


import com.sparta.mulmul.item.itemDto.ItemUserResponseDto;
import lombok.Getter;

import java.util.List;

@Getter
public class UserStoreResponseDto {
    private String nickname;
    private String profile;
    private String degree;
    private int totalPoint;
    private int degreePoint;
    private String address;
    private String storeInfo;
    private List<ItemUserResponseDto> itemList;

    // 이승재 / 유저 스토어 목록 보기
    public UserStoreResponseDto(String nickname, String profile, String degree, int totalPoint, int degreePoint, String address, String storeInfo, List<ItemUserResponseDto> itemList){
        this.nickname = nickname;
        this.profile = profile;
        this.degree = degree;
        this.totalPoint = totalPoint;
        this.degreePoint = degreePoint;
        this.address = address;
        this.storeInfo = storeInfo;
        this.itemList = itemList;
    }
}
