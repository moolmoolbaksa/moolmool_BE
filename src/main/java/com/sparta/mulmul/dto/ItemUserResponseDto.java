package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemUserResponseDto{
    private Long itemId;
    private String itemImg;
    private String status;

    public ItemUserResponseDto(Long itemId, String itemImg, String status) {
        this.itemId = itemId;
        this.itemImg = itemImg;
        this.status = status;
    }
}