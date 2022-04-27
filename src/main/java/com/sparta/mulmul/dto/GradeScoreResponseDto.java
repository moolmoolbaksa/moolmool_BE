package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GradeScoreResponseDto {
    private boolean ok;

    public GradeScoreResponseDto(boolean ok) {
        this.ok = ok;
    }
}
