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

import javax.transaction.Transactional;

// 성훈 - 평점 평가페이지 - 평점 보여주기
@RequiredArgsConstructor
@Service
public class ScoreService {

    private final UserRepository userRepository;
    private final BarterRepository barterRepository;


    // 성훈 - 평가 페이지 보여주기
    public OppentScoreResponseDto showOppentScore(Long barterId, UserDetailsImpl userDetails) {

        // 거래내역을 조회
        Barter mybarter = barterRepository.getById(barterId);
        Long userId = userDetails.getUserId();

        // 만약 바이어Id와 로그인 유저가 동일하면, 상대방의 아이디를 셀러Id로 식별
        if (mybarter.getBuyerId().equals(userId)) {
            Long oppenetId = mybarter.getSellerId();
            User OppentUser = userRepository.getById(oppenetId);
            // 상대방의 정보를 조회
            return new OppentScoreResponseDto(
                    oppenetId,
                    OppentUser.getProfile(),
                    OppentUser.getNickname(),
                    OppentUser.getGrade()
            );
        }
        // 만약 바이어Id와 로그인 유저Id가 다르다면, 상대방의 아이디를 바이어Id로 식별
        Long oppenetId = mybarter.getBuyerId();
        User OppentUser = userRepository.getById(oppenetId);
        // 상대방의 정보를 조회
        return new OppentScoreResponseDto(
                oppenetId,
                OppentUser.getProfile(),
                OppentUser.getNickname(),
                OppentUser.getGrade()
        );
    }

    // 성훈 - 상대 평점주기
    @Transactional
    public GradeScoreResponseDto gradeScore(GradeScoreRequestDto gradeScoreRequestDto, UserDetailsImpl userDetails) {
        // 상대 userId
        Long oppentUserId = gradeScoreRequestDto.getUserId();
        // A/B/C/D/F 평가주기
        String gradeScore = gradeScoreRequestDto.getScore();
        // 상대찾기
        User oppentUser = userRepository.getById(oppentUserId);
        // 상대 등급
        String oppentDegree;
        // 상대의 총점수
        float oppentUserTotalGrade = oppentUser.getTotalGrade();
        // 상대 평가점수
        float oppentGrade;
        // 상대 평가자 수
        int oppentRaterCnt = oppentUser.getRaterCount();
        // 점수
        float gradeFloat;

        //협의 중
        switch (gradeScore) {
            case "A":
                gradeFloat = 5.0f;
                oppentUserTotalGrade = oppentUserTotalGrade + gradeFloat;
                break;
            case "B":
                gradeFloat = 4.0f;
                oppentUserTotalGrade = oppentUserTotalGrade + gradeFloat;
                break;
            case "C":
                gradeFloat = 3.0f;
                oppentUserTotalGrade = oppentUserTotalGrade + gradeFloat;
                break;
            case "D":
                gradeFloat = 2.0f;
                oppentUserTotalGrade = oppentUserTotalGrade + gradeFloat;
                break;
            case "F":
                gradeFloat = 1.0f;
                oppentUserTotalGrade = oppentUserTotalGrade + gradeFloat;
                break;
        }

        // 평가자수 +1
        oppentRaterCnt = oppentRaterCnt + 1;
        // 유저평가 계산법 논의필요
        oppentGrade = oppentUserTotalGrade / oppentRaterCnt;

        //임의로 넣어줌 -> 상의해야됨
        if (oppentUserTotalGrade >= 20.0f) {
            oppentDegree = "물물박사";
        } else if (oppentUserTotalGrade >= 15.0f) {
            oppentDegree = "물물석사";
        } else if (oppentUserTotalGrade >= 10.0f) {
            oppentDegree = "물물학사";
        } else if (oppentUserTotalGrade >= 5.0f) {
            oppentDegree = "물물학생";
        } else {
            oppentDegree = "물물어린이";
        }

        oppentUser.updateScore(
                oppentUserTotalGrade,
                oppentGrade,
                oppentRaterCnt,
                oppentDegree
        );

        return new GradeScoreResponseDto(true);
    }
}
