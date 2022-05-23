package com.sparta.mulmul.dto.barter;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Builder
@NoArgsConstructor
public class BarterHotItemListDto {
    private Long itemId;
    private String title;
    private String itemImg;
    private String contents;
    private int status;

    //성훈 - 거래내역
    @QueryProjection
    public BarterHotItemListDto(Long itemId, String title, String itemImg, String contents, int status) {
        this.itemId = itemId;
        this.title = title;
        this.itemImg = itemImg;
        this.contents = contents;
        this.status = status;
    }
}



