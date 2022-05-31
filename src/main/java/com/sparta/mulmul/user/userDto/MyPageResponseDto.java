package com.sparta.mulmul.user.userDto;

import com.sparta.mulmul.item.itemDto.ItemUserResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyPageResponseDto {
    private String nickname;
    private String profile;
    private String degree;
    private int totalPoint;
    private int degreePoint;
    private Long acceptorCnt;
    private Long requesterCnt;
    private int scoreCnt;
    private String address;
    private String storeInfo;
    private List<ItemUserResponseDto> itemList;
    private List<ItemUserResponseDto> myScrabList;

    // 성훈 - 마이페이지 전체 조회
    public MyPageResponseDto(String nickname, String profile, String degree, int totalPoint, int degreePoint, Long acceptorCnt, Long requesterCnt, int scoreCnt,
                             String address, String storeInfo, List<ItemUserResponseDto> itemList, List<ItemUserResponseDto> myScrabList) {
        this.nickname = nickname;
        this.profile = profile;
        this.degree = degree;
        this.totalPoint = totalPoint;
        this.degreePoint = degreePoint;
        this.acceptorCnt = acceptorCnt;
        this.requesterCnt = requesterCnt;
        this.scoreCnt = scoreCnt;
        this.address = address;
        this.storeInfo = storeInfo;
        this.itemList = itemList;
        this.myScrabList = myScrabList;
    }
}
