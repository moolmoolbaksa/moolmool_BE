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
public class BarterIAlltemListDto {
    private Long itemId;
    private Long barterId;
    private String title;
    private String itemImg;
    private String contents;

    //성훈 - 거래내역
    @QueryProjection
    public BarterIAlltemListDto(Long itemId, Long barterId, String title, String itemImg, String contents) {
        this.itemId = itemId;
        this.barterId = barterId;
        this.title = title;
        this.itemImg = itemImg;
        this.contents = contents;
    }
}



