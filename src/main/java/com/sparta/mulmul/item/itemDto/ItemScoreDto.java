package com.sparta.mulmul.item.itemDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemScoreDto {
    private Long itemId;
    private String itemImg;

    public ItemScoreDto(Long itemId, String itemImg) {
        this.itemId = itemId;
        this.itemImg = itemImg;
    }
}
