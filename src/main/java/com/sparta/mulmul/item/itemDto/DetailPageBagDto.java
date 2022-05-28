package com.sparta.mulmul.item.itemDto;

import lombok.Getter;

@Getter
public class DetailPageBagDto {
    private String bagImg;
    private Long bagItemId;

    public DetailPageBagDto(String bagImg, Long bagItemId){
        this.bagImg = bagImg;
        this.bagItemId = bagItemId;
    }
}
