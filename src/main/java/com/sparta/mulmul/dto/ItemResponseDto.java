package com.sparta.mulmul.dto;


import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class ItemResponseDto {
    private Long itemId;
    private String itemImg;

    public ItemResponseDto(Long itemId, String itemImg) {
        this.itemId = itemId;
        this.itemImg = itemImg;
    }

}
