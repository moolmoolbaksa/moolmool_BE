package com.sparta.mulmul.dto.barter;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BarterMessageDto {
    private  Long barterId;
    private Boolean isTrade;
    private int status;

    public BarterMessageDto(Long barterId, Boolean isTrade, int status) {
        this.barterId = barterId;
        this.isTrade = isTrade;
        this.status = status;
    }
}
