package com.sparta.mulmul.dto;


import lombok.Getter;

@Getter
public class MyScrabItemDto {
    private Long itemId;
    private String title;
    private String contents;
    private String image;


    public MyScrabItemDto(Long itemId, String title, String contents, String image){
        this.itemId  = itemId;
        this.title = title;
        this.contents = contents;
        this.image = image;
    }
}
