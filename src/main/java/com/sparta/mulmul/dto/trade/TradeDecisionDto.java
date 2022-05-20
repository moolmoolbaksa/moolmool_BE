package com.sparta.mulmul.dto.trade;

import lombok.Getter;

import java.util.List;

@Getter
public class TradeDecisionDto {
    private String opponentNickname;
    private String nickname;
    private String degree;
    private String title;
    private String contents;
    private String image;
    private String accepted;
    private List<TradeInfoImagesDto> barterItem;


    public TradeDecisionDto(String opponentNickname, String nickname, String degree, String title, String contents, String image, String accepted,List<TradeInfoImagesDto> barterItem){
        this.opponentNickname = opponentNickname;
        this.nickname = nickname;
        this.degree = degree;
        this.title = title;
        this.contents = contents;
        this.image = image;
        this.accepted = accepted;
        this.barterItem = barterItem;
    }
}