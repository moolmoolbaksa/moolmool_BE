package com.sparta.mulmul.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class TradeDecisionDto {
    private String buyerNickName;
    private String sellerNickName;
    private String sellerItemImage;
    private List<String> buyerItemImages;

    public TradeDecisionDto(String buyerNickName, String sellerNickName, String sellerItemImage, List<String> buyerItemImages){
        this.buyerNickName = buyerNickName;
        this.sellerNickName = sellerNickName;
        this.sellerItemImage = sellerItemImage;
        this.buyerItemImages = buyerItemImages;
    }

}
