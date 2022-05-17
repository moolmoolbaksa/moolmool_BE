package com.sparta.mulmul.dto.item;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemStarDto {
    private Long itemId;
    private String image;
    private String title;
    private String contensts;

    public ItemStarDto(Long itemId, String image, String title, String contensts) {
        this.itemId = itemId;
        this.image = image;
        this.title = title;
        this.contensts = contensts;
    }
}