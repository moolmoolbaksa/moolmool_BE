package com.sparta.mulmul.dto;


import lombok.Getter;

import java.util.List;

@Getter
public class TradeInfoDto {
    private String opponentNickName;
    private String opponentImage;
    private String myNickName;
    private List<TradeInfoImagesDto> myImages;

    public TradeInfoDto(String opponentNickName, String opponentImage, String myNickName, List<TradeInfoImagesDto> tradeInfoImagesDtoArrayList){
        this.opponentNickName = opponentNickName;
        this.opponentImage = opponentImage;
        this.myNickName = myNickName;
        this.myImages = tradeInfoImagesDtoArrayList;
    }

}
