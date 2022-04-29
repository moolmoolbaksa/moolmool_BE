package com.sparta.mulmul.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Builder
public class MyBarterDto {
    private Long itemId;
    private String title;
    private String itemImg;
    private LocalDateTime date;
    private String status;

    //성훈 - 거래내역
    public MyBarterDto(Long itemId, String title, String itemImg, LocalDateTime date, String status) {
        this.itemId = itemId;
        this.title = title;
        this.itemImg = itemImg;
        this.date = date;
        this.status = status;
    }

}
