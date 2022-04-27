package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OppentScoreResponseDto {
    private Long userId;
    private String profile;
    private String nickname;
    private float grade;

    public OppentScoreResponseDto(Long userId, String profile, String nickname, float grade) {
        this.userId = userId;
        this.nickname = nickname;
        this.profile = profile;
        this.grade = grade;
    }
}
