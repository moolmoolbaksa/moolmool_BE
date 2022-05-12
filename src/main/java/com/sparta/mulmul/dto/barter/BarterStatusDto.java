package com.sparta.mulmul.dto.barter;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BarterStatusDto {
    private Boolean isTrade;
    private Boolean isScore;
    private int status;

    public BarterStatusDto(Boolean isTrade, Boolean isScore, int status) {
        this.isTrade = isTrade;
        this.isScore = isScore;
        this.status = status;
    }
}
