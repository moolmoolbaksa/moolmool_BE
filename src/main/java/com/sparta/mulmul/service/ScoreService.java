package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.GradeScoreRequestDto;
import com.sparta.mulmul.dto.OppentScoreResponseDto;
import com.sparta.mulmul.model.Barter;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.BarterRepository;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

// 성훈 - 평점 평가페이지 - 평점 보여주기
@RequiredArgsConstructor
@Service
public class ScoreService {

    private final UserRepository userRepository;
    private final BarterRepository barterRepository;


    // 성훈 - 평가 페이지 보여주기
    public OppentScoreResponseDto showOppentScore(Long barterId, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("user not found")
        );

        // 거래내역을 조회
        Barter myBarter = barterRepository.findById(barterId).orElseThrow(() -> new IllegalArgumentException("barter not found"));
        Long userId = userDetails.getUserId();

        // 바이어Id와 셀러Id에 유저아이디가 없을 경우
        if (myBarter.getBuyerId() != userId && myBarter.getSellerId() != userId){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "올바른 요청이 아닙니다");
        }

        // 만약 바이어Id와 로그인 유저가 동일하면, 상대방의 아이디를 셀러Id로 식별
        if (myBarter.getBuyerId().equals(userId)) {
            Long oppenetId = myBarter.getSellerId();
            User OppentUser = userRepository.findById(oppenetId).orElseThrow(() -> new IllegalArgumentException("user not found"));
            ;
            // 상대방의 정보를 조회
            return new OppentScoreResponseDto(
                    oppenetId,
                    OppentUser.getProfile(),
                    OppentUser.getNickname(),
                    OppentUser.getGrade()
            );
        } else {
            // 만약 바이어Id와 로그인 유저Id가 다르다면, 상대방의 아이디를 바이어Id로 식별
            Long oppenetId = myBarter.getBuyerId();
            User OppentUser = userRepository.findById(oppenetId).orElseThrow(() -> new IllegalArgumentException("user not found"));
            ;
            // 상대방의 정보를 조회
            return new OppentScoreResponseDto(
                    oppenetId,
                    OppentUser.getProfile(),
                    OppentUser.getNickname(),
                    OppentUser.getGrade()
            );

        }

    }

    // 성훈 - 상대 평점주기

    @Transactional
    public void gradeScore(GradeScoreRequestDto gradeScoreRequestDto, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("user not found")
        );


        // 상대 userId
        Long oppentUserId = gradeScoreRequestDto.getUserId();
        // 평가주기
        float gradeScore = gradeScoreRequestDto.getScore();
        // 상대찾기
        User oppentUser = userRepository.findById(oppentUserId).orElseThrow(() -> new IllegalArgumentException("user not found"));
        // 상대 등급
        String oppentDegree;
        // 상대의 총점수
        float oppentUserTotalGrade = oppentUser.getTotalGrade();
        // 상대 평가점수
        float oppentGrade;
        // 상대 평가자 수
        int oppentRaterCnt = oppentUser.getRaterCount();
        // 점수

        // 거래내역 조회
        Long barterId = gradeScoreRequestDto.getBarterId();
        Barter barter = barterRepository.findById(barterId).orElseThrow(() -> new IllegalArgumentException("barter not found"));
        // 이미 평가를 완료한 경우
        if (barter.getStatus() == 2) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이미 평가한 거래내역입니다.");
            // 거래내역의 상대 Id와 Request로 전달받은 상대방의 정보와 다를 경우
        } else if ((oppentUserId != barter.getBuyerId()) && (oppentUserId != barter.getSellerId())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "올바른 요청이 아닙니다");
            // 자기 자신에게 점수를 줄 경우
        } else if (oppentUserId == user.getId()){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "올바른 요청이 아닙니다");
            // 거래가 완료되지 않았을 경우
        } else if (barter.getStatus() == 0){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "올바른 요청이 아닙니다");
        }

        //협의 중 -> 총점수 = 이전의 총점수 + 평가된 평점
        oppentUserTotalGrade = oppentUserTotalGrade + gradeScore;
        // 평가자수 +1
        oppentRaterCnt = oppentRaterCnt + 1;
        // 유저의 평균 평점
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
        // 유저 정보를 업데이트 이후 status를 거래완료(1) -> 평가완료(2)으로 업데이트를 한다.
        barter.updatebarter(2);
    }
}
