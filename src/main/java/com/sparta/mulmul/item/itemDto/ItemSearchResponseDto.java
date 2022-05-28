package com.sparta.mulmul.item.itemDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemSearchResponseDto {
    private Long itemId;
    private String nickname;
    private String categroy;
    private String title;
    private String contents;
    private String image;
    private String address;
    private int scrabCnt;
    private int viewCnt;
    private int status;
    private boolean isScrab;

    // 이승재 / 아이템 검색
    public ItemSearchResponseDto(Long itemId,
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
}
