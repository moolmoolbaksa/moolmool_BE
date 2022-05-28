package com.sparta.mulmul.item.itemDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ItemUpdateRequestDto {
    private String category;
    private List<String> favored;
    private String title;
    private String contents;
    private List<String> imagesUrl;
    private List<String> images;
    private String type;

    public ItemUpdateRequestDto(String category, List<String> favored, String title, String contents,List<String> imagesUrl, List<String> images, String type) {
        this.category = category;
        this.favored = favored;
        this.title = title;
        this.contents = contents;
        this.imagesUrl = imagesUrl;
        this.images = images;
        this.type = type;
    }
}
