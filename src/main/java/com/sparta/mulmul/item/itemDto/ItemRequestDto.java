package com.sparta.mulmul.item.itemDto;

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


    //이승재 / 보따리 아이템 등록하기용 Dto
    public ItemRequestDto(String category, List<String> favored, String title, String contents, List<String> imgUrl, String type ){
        this.category = category;
        this.favored = favored;
        this.title = title;
        this.contents = contents;
        this.imgUrl = imgUrl;
        this.type = type;
    }
}