package com.sparta.mulmul.dto.barter;


import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class OpponentBarterDto {
    private Long itemId;
    private String title;
    private String itemImg;
    private String contents;

    //성훈 - 거래내역
    public OpponentBarterDto(Long itemId, String title, String itemImg, String contents) {
        this.itemId = itemId;
        this.title = title;
        this.itemImg = itemImg;
        this.contents = contents;
    }
}



