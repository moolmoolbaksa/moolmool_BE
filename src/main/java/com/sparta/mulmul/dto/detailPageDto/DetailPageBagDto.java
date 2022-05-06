package com.sparta.mulmul.dto.detailPageDto;

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
