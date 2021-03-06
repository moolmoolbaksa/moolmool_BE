package com.sparta.mulmul.item.scrabDto;


import lombok.Getter;

@Getter
public class MyScrabItemDto {
    private Long itemId;
    private String title;
    private String contents;
    private String image;
    private int status;

    public MyScrabItemDto(Long itemId, String title, String contents, String image, int status){
        this.itemId  = itemId;
        this.title = title;
        this.contents = contents;
        this.image = image;
        this.status = status;
    }
}
