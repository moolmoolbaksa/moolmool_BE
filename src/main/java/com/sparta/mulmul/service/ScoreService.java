package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.GradeScoreRequestDto;
import com.sparta.mulmul.dto.GradeScoreResponseDto;
import com.sparta.mulmul.dto.OppentScoreResponseDto;
import com.sparta.mulmul.model.Barter;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.BarterRepository;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 성훈 - 평점 평가페이지 - 평점 보여주기
@RequiredArgsConstructor
@Service
public class ScoreService {

    private final UserRepository userRepository;
    private final BarterRepository barterRepository;


    // 성훈 - 평가 페이지 보여주기
    public OppentScoreResponseDto showOppentScore(Long barterId,   UserDetailsImpl userDetails) {

        Barter mybarter = barterRepository.getById(barterId);
        Long userId = userDetails.getUserId();

        if (mybarter.getBuyerId() == userId){
            Long oppenetId = mybarter.getSellerId();
            User OppentUser = userRepository.getById(oppenetId);

            String profile = OppentUser.getProfile();
            String nickname = OppentUser.getNickname();
            float grade = OppentUser.getGrade();

            OppentScoreResponseDto opentScore = new OppentScoreResponseDto(oppenetId, profile, nickname, grade);
            return opentScore;
        }

        Long oppenetId = mybarter.getBuyerId();
        User OppentUser = userRepository.getById(oppenetId);

        String profile = OppentUser.getProfile();
        String nickname = OppentUser.getNickname();
        float grade = OppentUser.getGrade();

        OppentScoreResponseDto opentScore = new OppentScoreResponseDto(userId, profile, nickname, grade);
        return opentScore;
    }

    // 성훈 - 상대 평점주기
    public GradeScoreResponseDto gradeScore(GradeScoreRequestDto gradeScoreRequestDto, UserDetailsImpl userDetails){
        GradeScoreResponseDto gradeScoreResponseDto = null;
        // 상대 user
        Long oppentUserId = gradeScoreRequestDto.getUserId();
//        String gradeScore = gradeScoreRequestDto.getScore();
        User oppentUser = userRepository.getById(oppentUserId);
        float oppentUserTotalGrade = oppentUser.getTotalGrade();
        float oppentIserGrad = oppentUser.getGrade();
        int oppentRaterCnt = oppentUser.getRaterCount();
        String oppentDegree = oppentUser.getDegree();
//        float score = 0;

        // 협의 중
//        if (gradeScore=="A"){
//            score = 4;
//            oppentUserTotalGrade = oppentUserTotalGrade + score;
//        } else if (gradeScore=="B") {
//            score = 3;
//            oppentUserTotalGrade = oppentUserTotalGrade + score;
//        } else if (gradeScore=="C") {
//            score = 2;
//            oppentUserTotalGrade = oppentUserTotalGrade + score;
//        } else if (gradeScore=="D") {
//            score = 1;
//            oppentUserTotalGrade = oppentUserTotalGrade + score;
//        } else if (gradeScore=="F") {
//            score = 0;
//            oppentUserTotalGrade = oppentUserTotalGrade + score;
//        }

        // 유저평가 계산법 논의필요
//        oppentIserGrad = oppentUserTotalGrade /5;
        // 평가자수 +1
        oppentRaterCnt = oppentRaterCnt + 1;

        //임의로 넣어줌 -> 상의해야됨
        oppentDegree = "물물박사";

        oppentUser.updateScore(oppentUserTotalGrade, oppentIserGrad, oppentRaterCnt, oppentDegree);

        gradeScoreResponseDto = new GradeScoreResponseDto(true);
        return gradeScoreResponseDto;
    }
}
