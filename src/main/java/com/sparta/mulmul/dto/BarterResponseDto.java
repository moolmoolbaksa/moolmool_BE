package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BarterResponseDto {
    private Long barterId;
    private List<MyBarterDto> myItem;
    private List<MyBarterDto> barterList;


    public  BarterResponseDto(Long barterId, List<MyBarterDto> myItem, List<MyBarterDto> barterList) {
        this.barterId = barterId;
        this.myItem = myItem;
        this.barterList = barterList;
    }
}
