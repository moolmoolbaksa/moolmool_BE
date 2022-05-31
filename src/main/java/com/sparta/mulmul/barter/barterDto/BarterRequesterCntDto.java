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
public class BarterRequesterCntDto {
    private Long requesterCnt;



    @QueryProjection
    public BarterRequesterCntDto(Long requesterCnt) {
        this.requesterCnt = requesterCnt;
    }
}
