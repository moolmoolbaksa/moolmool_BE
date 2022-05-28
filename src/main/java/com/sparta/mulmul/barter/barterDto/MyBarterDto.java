package com.sparta.mulmul.barter.barterDto;


import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class MyBarterDto {
    private Long itemId;
    private String title;
    private String itemImg;

    //성훈 - 거래내역
    public MyBarterDto(Long itemId, String title, String itemImg) {
        this.itemId = itemId;
        this.title = title;
        this.itemImg = itemImg;

    }


    public void add(Long eachBuyerId, String buyerItemTitle, String buyerItemImg) {
        this.itemId = eachBuyerId;
        this.title = buyerItemTitle;
        this.itemImg = buyerItemImg;
    }
}
