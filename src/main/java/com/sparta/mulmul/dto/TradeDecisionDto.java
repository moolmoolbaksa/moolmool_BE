package com.sparta.mulmul.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class TradeDecisionDto {
    private Long userId;
    private String nickname;
    private String degree;
    private String title;
    private String contents;
    private String image;
    private List<TradeInfoImagesDto> barterItem;

    public TradeDecisionDto(Long userId, String nickname, String degree, String title, String contents, String image, List<TradeInfoImagesDto> barterItem){
        this.userId = userId;
        this.nickname = nickname;
        this.degree = degree;
        this.title = title;
        this.contents = contents;
        this.image = image;
        this.barterItem = barterItem;
    }
}