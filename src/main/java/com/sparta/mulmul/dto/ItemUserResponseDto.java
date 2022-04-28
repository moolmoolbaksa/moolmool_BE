package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemUserResponseDto{
    private Long itemId;
    private String itemImg;

    public ItemUserResponseDto(Long itemId, String itemImg) {
        this.itemId = itemId;
        this.itemImg = itemImg;
    }
}
