package com.sparta.mulmul.dto.user;

import com.sparta.mulmul.dto.item.ItemUserResponseDto;
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
    private List<ItemUserResponseDto> myScrabList;

    // 성훈 - 마이페이지 전체 조회
    public MyPageResponseDto(String nickname, String profile, String degree, float grade, String address, String storeInfo, List<ItemUserResponseDto> itemList, List<ItemUserResponseDto> myScrabList) {
        this.nickname = nickname;
        this.profile = profile;
        this.degree = degree;
        this.grade = grade;
        this.address = address;
        this.storeInfo = storeInfo;
        this.itemList = itemList;
        this.myScrabList = myScrabList;
    }
}
