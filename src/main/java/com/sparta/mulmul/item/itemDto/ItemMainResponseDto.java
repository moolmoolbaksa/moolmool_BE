package com.sparta.mulmul.item.itemDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ItemMainResponseDto {
    private Long totalCnt;
    private List<ItemResponseDto> items;

    public ItemMainResponseDto (Long totalCnt, List<ItemResponseDto> items){
        this.totalCnt = totalCnt;
        this.items = items;
    }

}
