package com.sparta.mulmul.dto;


import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class MyBarterScorDto {
    private Long itemId;
    private String itemImg;

    //성훈 - 거래내역
    public MyBarterScorDto(Long itemId, String itemImg) {
        this.itemId = itemId;
        this.itemImg = itemImg;
    }

}
