package com.sparta.mulmul.dto.item;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Builder
@NoArgsConstructor
public class ItemUserResponseDto {
    private Long itemId;
    private String image;
    private int status;

    @QueryProjection
    public ItemUserResponseDto(Long itemId, String image, int status) {
        this.itemId = itemId;
        this.image = image;
        this.status = status;
    }
}