package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GradeScoreRequestDto {
    private Long userId;
    private String score;

    public GradeScoreRequestDto(Long userId, String score) {
        this.userId = userId;
        this.score = score;
    }
}
