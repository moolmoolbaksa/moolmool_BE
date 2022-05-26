package com.sparta.mulmul.dto.barter;


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
