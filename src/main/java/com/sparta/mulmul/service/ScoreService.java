package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.GradeScoreRequestDto;
import com.sparta.mulmul.dto.MyBarterScorDto;
import com.sparta.mulmul.dto.OppentScoreResponseDto;
import com.sparta.mulmul.model.Barter;
import com.sparta.mulmul.model.Item;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.BarterRepository;
import com.sparta.mulmul.repository.ItemRepository;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

// 성훈 - 평점 평가페이지 - 평점 보여주기
@RequiredArgsConstructor
@Service
public class ScoreService {

    private final UserRepository userRepository;
    private final BarterRepository barterRepository;
    private final ItemRepository itemRepository;


    // 성훈 - 평가 페이지 보여주기
    public OppentScoreResponseDto showOppentScore(Long barterId, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("user not found")
        );

        Long userId = userDetails.getUserId();

        // 내가 거래한 거래리스트를 대입한다.
        // barterId, buyerId, SellerId를 분리한다.
        Barter mybarter = barterRepository.findById(barterId).orElseThrow(
                () -> new IllegalArgumentException("barter not found")
        );

            // 거래 물품리스트를 담을 Dto
            List<MyBarterScorDto> myBarterList = new ArrayList<>();
            List<MyBarterScorDto> barterList = new ArrayList<>();

            String barter = mybarter.getBarter();
            //barter 거래내역 id split하기 -> 파싱하여 거래항 물품의 Id값을 찾기
            String[] barterIds = barter.split(";");
            String buyerItemIdList = barterIds[0].split(",")[0];
            String sellerItemIdList = barterIds[1];


            // 바이어(유저)의 물품을 찾아서 정보를 넣기
                Long itemIdB = Long.parseLong( buyerItemIdList);
                System.out.println("바이어 아이디 " + itemIdB);
                Item buyerItem = itemRepository.findById(itemIdB).orElseThrow(
                        () -> new IllegalArgumentException("buyerItem not found")
                );

                MyBarterScorDto buyerItemList = new MyBarterScorDto(
                        itemIdB,
                        buyerItem.getItemImg()
                );

                if (buyerItem.getBag().getUserId().equals(userId)) {
                    myBarterList.add(buyerItemList);
                } else {
                    barterList.add(buyerItemList);
                }

            //셀러(유저)의 물품을 찾아서 정보를 넣기
                Long itemIdS = Long.parseLong(sellerItemIdList);
                Item sellerItem = itemRepository.findById(itemIdS).orElseThrow(
                        () -> new IllegalArgumentException("sellerItem not found")
                );

                MyBarterScorDto sellerItemList = new MyBarterScorDto(
                        itemIdS,
                        sellerItem.getItemImg());

                if (sellerItem.getBag().getUserId().equals(userId)) {
                    myBarterList.add(sellerItemList);
                } else {
                    barterList.add(sellerItemList);
                }


            // 거래내역을 조회
            Barter myBarter = barterRepository.findById(barterId).orElseThrow(() -> new IllegalArgumentException("barter not found"));
                // 바이어Id와 셀러Id에 유저아이디가 없을 경우
                if (myBarter.getBuyerId() != userId && myBarter.getSellerId() != userId) {
                    throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "올바른 요청이 아닙니다");
                }

            // 만약 바이어Id와 로그인 유저가 동일하면, 상대방의 아이디를 셀러Id로 식별
            if (myBarter.getBuyerId().equals(userId)) {
                Long oppenetId = myBarter.getSellerId();
                User OppentUser = userRepository.findById(oppenetId).orElseThrow(() -> new IllegalArgumentException("user not found"));

                // 상대방의 정보를 조회
                return new OppentScoreResponseDto(
                        oppenetId,
                        OppentUser.getNickname(),
                        myBarterList,
                        barterList
                );
            } else {
                // 만약 바이어Id와 로그인 유저Id가 다르다면, 상대방의 아이디를 바이어Id로 식별
                Long oppenetId = myBarter.getBuyerId();
                User OppentUser = userRepository.findById(oppenetId).orElseThrow(() -> new IllegalArgumentException("user not found"));

                // 상대방의 정보를 조회
                return new OppentScoreResponseDto(
                        oppenetId,
                        OppentUser.getNickname(),
                        myBarterList,
                        barterList
                );
            }
    }


        // 성훈 - 상대 평점주기
        @Transactional
        public void gradeScore (GradeScoreRequestDto gradeScoreRequestDto, UserDetailsImpl userDetails){
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
            if (barter.getStatus() <= 2) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "거래가 완료되지 않았습니다.");
            } else if (barter.getStatus() >= 4) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이미 평가한 거래내역입니다.");
                // 거래내역의 상대 Id와 Request로 전달받은 상대방의 정보와 다를 경우
            } else if ((oppentUserId != barter.getBuyerId()) && (oppentUserId != barter.getSellerId())) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "올바른 요청이 아닙니다");
                // 자기 자신에게 점수를 줄 경우
            } else if (oppentUserId == user.getId()) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "올바른 요청이 아닙니다");
                // 거래가 완료되지 않았을 경우
            } else if (barter.getStatus() == 0) {
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
            // 유저 정보를 업데이트 이후 status를 거래완료(3) -> 평가완료(4)으로 업데이트를 한다.
            barter.updatebarter(4);
        }
}
