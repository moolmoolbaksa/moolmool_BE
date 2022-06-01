package com.sparta.mulmul.item.itemDto;

import com.sparta.mulmul.item.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
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

    public static ItemStarDto createFrom(Item item){
        ItemStarDto starDto = new ItemStarDto();

        starDto.itemId = item.getId();
        String[] items = item.getItemImg().split(";");
        starDto.image = items[0];
        starDto.title = item.getTitle();
        starDto.contents = item.getContents();

        return starDto;
    }
}