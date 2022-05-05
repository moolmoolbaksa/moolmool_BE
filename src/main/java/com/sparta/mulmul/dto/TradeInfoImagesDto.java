package com.sparta.mulmul.dto;


import lombok.Getter;

@Getter
public class TradeInfoImagesDto {
    private Long itemId;
    private String image;

    public TradeInfoImagesDto(String itemImage, Long itemId) {
        this.itemId = itemId;
        this.image = itemImage;
    }

}
