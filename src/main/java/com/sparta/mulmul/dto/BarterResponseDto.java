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

    public void addNotFin(BarterNotFinDto notFinBarter) {
        System.out.println("거래중르라라랄" + notFinBarter.getBarterId());
        this.notFinBarter = notFinBarter;
        System.out.println("거래중르라라랄111" + notFinBarter.getBarterId());
        this.finBarter = null;
        System.out.println("거래중르라라랄222" + notFinBarter.getBarterId());
    }

    public void addFin(BarterFinDto finBarter) {
        this.notFinBarter = null;
        this.finBarter = finBarter;
    }
}
