package com.sparta.mulmul.item.itemDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemStarDto {
    private Long itemId;
    private String image;
    private String title;
    private String contents;

    public ItemStarDto(Long itemId, String image, String title, String contents) {
        this.itemId = itemId;
        this.image = image;
        this.title = title;
        this.contents = contents;
    }
}