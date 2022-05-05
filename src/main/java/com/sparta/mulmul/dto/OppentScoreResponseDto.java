package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OppentScoreResponseDto {
    private Long userId;
    private String nickname;
    private List<MyBarterScorDto> myItem;
    private List<MyBarterScorDto> barterItem;


    // 성훈 - 상대 평가 정보
    public OppentScoreResponseDto(Long userId, String nickname, List<MyBarterScorDto> myItem, List<MyBarterScorDto> barterItem) {
        this.userId = userId;
        this.nickname = nickname;
        this.myItem = myItem;
        this.barterItem = barterItem;
    }
}
