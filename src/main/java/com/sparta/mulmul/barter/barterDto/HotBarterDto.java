package com.sparta.mulmul.barter.barterDto;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Builder
@NoArgsConstructor
public class HotBarterDto {
    private String barter;


    //성훈 - 거래내역
    @QueryProjection
    public HotBarterDto(String barter) {
        this.barter = barter;
    }
}



