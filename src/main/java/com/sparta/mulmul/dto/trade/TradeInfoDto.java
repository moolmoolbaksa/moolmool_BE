package com.sparta.mulmul.dto.trade;


import lombok.Getter;

import java.util.List;

@Getter
public class TradeInfoDto {
    private String sellerNickName;
    private String sellerImages;
    private List<TradeInfoImagesDto> myImages;

    public TradeInfoDto(String sellerNickName, String sellerImages, List<TradeInfoImagesDto> tradeInfoImagesDtoArrayList){
        this.sellerNickName = sellerNickName;
        this.sellerImages = sellerImages;
        this.myImages = tradeInfoImagesDtoArrayList;
    }
}
