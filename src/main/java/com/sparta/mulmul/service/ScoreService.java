package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.GradeScoreRequestDto;
import com.sparta.mulmul.dto.MyBarterScorDto;
import com.sparta.mulmul.dto.OppentScoreResponseDto;
import com.sparta.mulmul.model.*;
import com.sparta.mulmul.repository.BarterRepository;
import com.sparta.mulmul.repository.ItemRepository;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.repository.chat.ChatMessageRepository;
import com.sparta.mulmul.repository.chat.ChatRoomRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// 성훈 - 평점 평가페이지 - 평점 보여주기
@RequiredArgsConstructor
@Service
public class ScoreService {

    private final UserRepository userRepository;
    private final BarterRepository barterRepository;
    private final ItemRepository itemRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;


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
        Long itemIdB = Long.parseLong(buyerItemIdList);
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
        if (!myBarter.getBuyerId().equals(userId) && !myBarter.getSellerId().equals(userId)) {
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
    public void gradeScore(GradeScoreRequestDto gradeScoreRequestDto, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("user not found")
        );


        // 상대 userId
        Long opponentUserId = gradeScoreRequestDto.getUserId();
        // 평가주기
        float gradeScore = gradeScoreRequestDto.getScore();
        // 상대찾기
        User opponentUser = userRepository.findById(opponentUserId).orElseThrow(() -> new IllegalArgumentException("user not found"));
        // 상대 등급
        String opponentDegree;
        // 상대의 총점수
        float opponentUserTotalGrade = opponentUser.getTotalGrade();
        // 상대 평가점수
        float opponentGrade = opponentUser.getGrade();
        // 상대 평가자 수
        int opponentRaterCnt = opponentUser.getRaterCount();
        // 점수

        // 거래내역 조회
        Long barterId = gradeScoreRequestDto.getBarterId();
        Barter barter = barterRepository.findById(barterId).orElseThrow(() -> new IllegalArgumentException("barter not found"));

        System.out.println("거래상태 : " + barter.getStatus());
        System.out.println("2번째 예외처리 : " + opponentUserId);
        System.out.println("바이어아이디 : " + barter.getBuyerId());
        System.out.println("셀러아이디 : " + barter.getSellerId());


        // 이미 평가를 완료한 경우
        if (barter.getStatus() != 3) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "올바른 요청이 아닙니다");
            // 거래내역의 상대 Id와 Request로 전달받은 상대방의 정보와 다를 경우
        } else if ((!opponentUserId.equals(barter.getBuyerId())) && (!opponentUserId.equals(barter.getSellerId()))) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "올바른 요청이 아닙니다");
            // 자기 자신에게 점수를 줄 경우
        } else if (opponentUserId.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "올바른 요청이 아닙니다");
        }

        // 10분이네 응답 보너스
        // 내 포지션 확인
        String myPosition = null;
        if (barter.getSellerId().equals(opponentUserId)) {
            myPosition = "buyer";
        } else {
            myPosition = "seller";
        }

        String[] barterIdList = barter.getBarter().split(";");
        String[] buyerItemId = barterIdList[0].split(",");
        String sellerItemId = barterIdList[1];

        int viewBonusCnt = 0;
        int scrabBonusCnt = 0;

        int status = 4;
        for (String eachBuyer : buyerItemId) {
            Long buyerId = Long.valueOf(eachBuyer);
            Item buyerItem = itemRepository.findById(buyerId).orElseThrow(
                    () -> new IllegalArgumentException("buyerItem not found"));
            buyerItem.statusUpdate(buyerItem.getId(), status);

            // 상대의 포지션이 바이어일 때, 조회수 100 이상이면 보너스 카운트 up!
            if (buyerItem.getViewCnt() >= 100 && !myPosition.equals("buyer")) {
                viewBonusCnt++;
            }
            // 상대의 포지션이 바이어일 때, 찜하기가 10 이상이면 보너스 카운트 up!
            if (buyerItem.getScrabCnt() >= 10 && !myPosition.equals("buyer")) {
                scrabBonusCnt++;
            }

            System.out.println("상대 바이어일 때 조회수 보너수 횟수 : " + viewBonusCnt);
            System.out.println("상대 바이어일 때 찜하기 보너스 횟수 : " + scrabBonusCnt);
        }
        //셀러(유저)의 물품을 찾아서 정보를 넣기
        Long sellerId = Long.parseLong(sellerItemId);
        Item sellerItem = itemRepository.findById(sellerId).orElseThrow(
                () -> new IllegalArgumentException("sellerItem not found")
        );
        sellerItem.statusUpdate(sellerItem.getId(), status);
        // 유저 정보를 업데이트 이후 status를 거래완료(3) -> 평가완료(4)으로 업데이트를 한다.
        barter.updateBarter(status);

        // 상대의 포지션이 셀러일 때, 조회수 100 이상이면 보너스 카운트 up!
        if (sellerItem.getViewCnt() >= 100 && !myPosition.equals("seller")) {
            viewBonusCnt++;
        }
        // 상대의 포지션이 셀러일 때, 찜하기가 10 이상이면 보너스 카운트 up!
        if (sellerItem.getScrabCnt() >= 10 && !myPosition.equals("seller")) {
            scrabBonusCnt++;
        }

        System.out.println("상대 셀러일 때 조회수 보너수 횟수 : " + viewBonusCnt);
        System.out.println("상대 셀러일 때 찜하기 보너스 횟수 : " + scrabBonusCnt);

        //        //협의 중 -> 총점수 = 이전의 총점수 + 평가된 평점
//        opponentUserTotalGrade = opponentUserTotalGrade + gradeScore;
        // 평가자수 +1
        opponentRaterCnt = opponentRaterCnt + 1;
        // 유저의 평균 평점
        if (opponentGrade == 0) {
            opponentGrade = gradeScore;
        } else {
            opponentGrade = (opponentGrade + gradeScore) / 2;
        }


        System.out.println("내 포지션은 : " + myPosition);

        ChatRoom chatRoomChek;
        List<ChatMessage> myMessageList;
        List<ChatMessage> opponentMessageList;

        // 내 포지션이 바이어라면 -> roomId를 모를 떄
        if (myPosition.equals("buyer")) {
            chatRoomChek = chatRoomRepository.findMyRequestChatRoom(user.getId(), opponentUserId);
            // 내포지션이 셀러라면
        } else {
            chatRoomChek = chatRoomRepository.findMyAcceptorIdChatRoom(user.getId(), opponentUserId);
        }
        System.out.println("채팅방 아이디 " + chatRoomChek.getId());
        System.out.println("유저아이디 " + user.getId());
        System.out.println("상대 아이디 :" + opponentUserId);
        Long chatRoomId = chatRoomChek.getId();
        Long myUserId = user.getId();
        myMessageList = chatMessageRepository.findMyChatMessage(myUserId, chatRoomId);
        opponentMessageList = chatMessageRepository.findMyChatMessage(opponentUserId, chatRoomId);

        System.out.println("채팅방 아이디는 : " + chatRoomChek.getRequesterId());

        // 각자의 최초 메시지의 시간을 조회한다
        int cnt = 0;
        LocalDateTime firstMyTime = null;
        LocalDateTime firstOppoentTime = null;
        for (ChatMessage myChat : myMessageList) {
            System.out.println("내 첫메시지 내용 : " + myChat.getMessage());
            firstMyTime = myChat.getCreatedAt();
            cnt++;
            if (cnt == 1) {
                break;
            }
        }
        cnt = 0;
        for (ChatMessage opponetChat : opponentMessageList) {
            System.out.println("상대의 첫 메시지 내용 : " + opponetChat.getMessage());
            firstOppoentTime = opponetChat.getCreatedAt();
            cnt++;
            if (cnt == 1) {
                break;
            }
        }


        System.out.println("내 첫메시지 : " + firstMyTime.getMinute());
        Long minute = ChronoUnit.MINUTES.between(firstMyTime, firstOppoentTime);

        System.out.println("비투인 사이 시간 " + minute);
        boolean chatTime = false;
        if (minute <= 10) {
            chatTime = true;
            System.out.println("10분 이내인가 : " + chatTime);
        }


        // 상대의 전체 점수에 1, 2은 - / 3은 0 / 4, 5은 +
        if (gradeScore <= 2) {
            opponentUserTotalGrade = opponentUserTotalGrade - (3.0f - gradeScore);
        } else if (gradeScore >= 4) {
            opponentUserTotalGrade = opponentUserTotalGrade + (gradeScore - 3.0f);
        }
        // 10분 이내 응답했을 때의 보너스
        if (chatTime) {
            opponentUserTotalGrade = opponentUserTotalGrade + 1.0f;
        }
//        // 거래를 5번 이상했고, 평균 평가점수가 4 이상이면 보너스
//        if (gradeScore >= 4 && opponentRaterCnt >= 5) {
//            opponentUserTotalGrade = opponentUserTotalGrade + 2.0f;
//        }
        // 완료된 거래내역의 조회수나 찜하기가 많으면 보너스
        if (viewBonusCnt >= 1){
            opponentUserTotalGrade = opponentUserTotalGrade + 1.0f*viewBonusCnt;
        }
        if (scrabBonusCnt>= 1){
            opponentUserTotalGrade = opponentUserTotalGrade + 1.0f*scrabBonusCnt;
        }

            //임의로 넣어줌 -> 상의해야됨
            if (opponentUserTotalGrade >= 150.0f) {
                opponentDegree = "물물박사";
            } else if (opponentUserTotalGrade >= 100.0f) {
                opponentDegree = "물물석사";
            } else if (opponentUserTotalGrade >= 70.0f) {
                opponentDegree = "물물학사";
            } else if (opponentUserTotalGrade >= 50.0f) {
                opponentDegree = "물물학생";
            } else {
                opponentDegree = "물물어린이";
            }

        opponentUser.updateScore(
                opponentUserTotalGrade,
                opponentGrade,
                opponentRaterCnt,
                opponentDegree
        );

    }
}
