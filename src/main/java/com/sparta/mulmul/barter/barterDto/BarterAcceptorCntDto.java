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
public class BarterAcceptorCntDto {
    private Long acceptorCnt;



    @QueryProjection
    public BarterAcceptorCntDto(Long acceptorCnt) {
        this.acceptorCnt = acceptorCnt;
    }
}
