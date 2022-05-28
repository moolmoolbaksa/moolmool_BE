package com.sparta.mulmul.barter;

import com.sparta.mulmul.barter.barterDto.BarterStatusDto;
import com.sparta.mulmul.barter.scoreDto.GradeScoreRequestDto;
import com.sparta.mulmul.barter.scoreDto.OppentScoreResponseDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ScoreController {
    final private ScoreService scoreService;

    // 성훈 - 상대 정보 보여주기
    @GetMapping("/user/score/{barterId}")
    public OppentScoreResponseDto showOppentScore(@PathVariable Long barterId,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails){

        return scoreService.showOppentScore(barterId, userDetails);
    }

    // 성훈 - 평가주기
    @PutMapping("/user/score")
    public BarterStatusDto showMyPageage (@RequestBody GradeScoreRequestDto gradeScoreRequestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails){
        return scoreService.gradeScore(gradeScoreRequestDto, userDetails);
    }
}
