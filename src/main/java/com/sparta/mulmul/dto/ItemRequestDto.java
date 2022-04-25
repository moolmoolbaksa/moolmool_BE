package com.sparta.mulmul.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ItemRequestDto {
    private String category;
    private List<String> favored;
    private String title;
    private String contents;
    private List<String> imgUrl;
    private String type;

    public ItemRequestDto(String category, List<String> favored, String title, String contents, List<String> imgUrl, String type) {
        this.category = category;
        this.favored = favored;
        this.title = title;
        this.contents = contents;
        this.imgUrl = imgUrl;
        this.type = type;
    }
}
