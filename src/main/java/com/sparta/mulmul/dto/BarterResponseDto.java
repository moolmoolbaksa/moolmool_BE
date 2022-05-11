package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class BarterResponseDto {
    private BarterNotFinDto notFinBarter;
    private BarterFinDto finBarter;

    public BarterResponseDto(BarterNotFinDto notFinBarter, BarterFinDto finBarter) {
        this.notFinBarter = notFinBarter;
        this.finBarter = finBarter;
    }
}
