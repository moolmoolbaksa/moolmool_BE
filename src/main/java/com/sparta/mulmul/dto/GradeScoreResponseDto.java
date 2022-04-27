package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GradeScoreResponseDto {
    private boolean ok;

// 성훈 - 평점주기
    public GradeScoreResponseDto(boolean ok) {
        this.ok = ok;
    }
}
