package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemUserResponseDto{
    private Long itemId;
    private String itemImg;
    private int status;

    public ItemUserResponseDto(Long itemId, String itemImg, int status) {
        this.itemId = itemId;
        this.itemImg = itemImg;
        this.status = status;
    }
}
