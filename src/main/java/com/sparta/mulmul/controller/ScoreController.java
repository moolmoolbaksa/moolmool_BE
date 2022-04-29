package com.sparta.mulmul.controller;

import com.sparta.mulmul.dto.GradeScoreRequestDto;
import com.sparta.mulmul.dto.GradeScoreResponseDto;
import com.sparta.mulmul.dto.OppentScoreResponseDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ScoreController {
    final private ScoreService scoreService;

    // 성훈 - 상대 정보 보여주기


    @GetMapping("/api/score/{barterid}")
    public OppentScoreResponseDto showOppentScore(@PathVariable Long barterId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return scoreService.showOppentScore(barterId, userDetails);
    }

    // 성훈 - 평가주기

    @PutMapping("/api/score")
    public GradeScoreResponseDto showMyPageage (@RequestBody GradeScoreRequestDto gradeScoreRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return scoreService.gradeScore(gradeScoreRequestDto, userDetails);
    }
}
