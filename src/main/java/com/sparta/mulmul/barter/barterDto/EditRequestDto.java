package com.sparta.mulmul.barter.barterDto;


import lombok.Getter;

import java.util.List;

@Getter
public class EditRequestDto {
    private Long barterId;
    private List<Long> itemId;

    public EditRequestDto(Long barterId, List<Long> itemId) {
        this.barterId = barterId;
        this.itemId = itemId;
    }
}
