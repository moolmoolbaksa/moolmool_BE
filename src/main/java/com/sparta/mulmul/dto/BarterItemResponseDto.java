package com.sparta.mulmul.dto;


import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class BarterItemResponseDto {
    private Long itemId;
    private String title;
    private String itemImg;
    private String date;
    private String status;

    //성훈 - 거래내역
    public BarterItemResponseDto(Long itemId, String title, String itemImg, String date, String status) {
        this.itemId = itemId;
        this.title = title;
        this.itemImg = itemImg;
        this.date = date;
        this.status = status;
    }

}
