package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BarterResponseDto {
    private Long barterId;
    private List<BarterItemResponseDto> myItem;

    public  BarterResponseDto(Long barterId, List<BarterItemResponseDto> myItem) {
        this.barterId = barterId;
        this.myItem = myItem;
    }
}
