package com.sparta.mulmul.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ItemResponseDto {
    private Long itemId;
    private String categroy;
    private String title;
    private String contents;
    private String image;
    private String address;
    private int scrabCnt;
    private int viewCnt;
    private int status;
    private boolean isScrab;


    // 이승재 / 전체 아이템 조회(카테고리별)
    public ItemResponseDto(Long itemId,
                           String categroy,
                           String title,
                           String contents,
                           String image,
                           String address,
                           int scrabCnt,
                           int viewCnt,
                           int status,
                           boolean isScrab) {
        this.itemId = itemId;
        this.categroy = categroy;
        this.title = title;
        this.contents = contents;
        this.image = image;
        this.address = address;
        this.scrabCnt = scrabCnt;
        this.viewCnt = viewCnt;
        this.status = status;
        this.isScrab = isScrab;
    }


    // 이승재 / 카테고리 값이 없을떄
    public ItemResponseDto(Long itemId,
                           String categroy,
                           String title,
                           String contents,
                           String image,
                           String address,
                           int scrabCnt,
                           int viewCnt,
                           int status) {
        this.itemId = itemId;
        this.categroy = categroy;
        this.title = title;
        this.contents = contents;
        this.image = image;
        this.address = address;
        this.scrabCnt = scrabCnt;
        this.viewCnt = viewCnt;
        this.status = status;

    }

}


