package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GradeScoreRequestDto {
    private Long userId;
    private String score;

    // 성훈 - 평점주기
    public GradeScoreRequestDto(Long userId, String score) {
        this.userId = userId;
        this.score = score;
    }
}
