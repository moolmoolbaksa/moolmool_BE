package com.sparta.mulmul.dto.score;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class GradeScoreRequestDto {
    private Long barterId;
    private Long userId;
    private float score;


    // 성훈 - 평점주기
    public GradeScoreRequestDto(Long barterId, Long userId, float score) {
        this.barterId = barterId;
        this.userId = userId;
        this.score = score;
    }
}
