package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemUserResponseDto{
    private Long itemId;
    private String image;
    private int status;

    public ItemUserResponseDto(Long itemId, String image, int status) {
        this.itemId = itemId;
        this.image = image;
        this.status = status;
    }
}