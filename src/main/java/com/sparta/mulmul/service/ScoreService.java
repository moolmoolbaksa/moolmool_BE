package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.NotificationDto;
import com.sparta.mulmul.dto.barter.BarterStatusDto;
import com.sparta.mulmul.dto.barter.MyBarterScorDto;
import com.sparta.mulmul.dto.score.GradeScoreRequestDto;
import com.sparta.mulmul.dto.score.OppentScoreResponseDto;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.model.Barter;
import com.sparta.mulmul.model.Item;
import com.sparta.mulmul.model.Notification;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.BarterRepository;
import com.sparta.mulmul.repository.ItemRepository;
import com.sparta.mulmul.repository.NotificationRepository;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.sparta.mulmul.exception.ErrorCode.*;

// 성훈 - 평점 평가페이지 - 평점 보여주기
@RequiredArgsConstructor
@Service
public class ScoreService {

    private final UserRepository userRepository;
    private final BarterRepository barterRepository;
    private final ItemRepository itemRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final NotificationRepository notificationRepository;


    // 성훈 - 평가 페이지 보여주기
    public OppentScoreResponseDto showOppentScore(Long barterId, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Long userId = userDetails.getUserId();
        // 내가 거래한 거래리스트를 대입한다.
        // barterId, buyerId, SellerId를 분리한다.
        Barter mybarter = barterRepository.findById(barterId).orElseThrow(() -> new CustomException(NOT_FOUND_BARTER));
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
        Item buyerItem = itemRepository.findById(itemIdB).orElseThrow(() -> new CustomException(NOT_FOUND_BUYER_ITEM));

        MyBarterScorDto buyerItemList = getMyBarterScorDto(itemIdB, buyerItem);
        splitAddList(
                buyerItem,
                userId,
                myBarterList,
                buyerItemList,
                barterList
        );
        //셀러(유저)의 물품을 찾아서 정보를 넣기
        Long itemIdS = Long.parseLong(sellerItemIdList);
        Item sellerItem = itemRepository.findById(itemIdS).orElseThrow(() -> new CustomException(NOT_FOUND_SELLER_ITEM));
        MyBarterScorDto sellerItemList = getMyBarterScorDto(itemIdS, sellerItem);
        splitAddList(
                sellerItem,
                userId,
                myBarterList,
                sellerItemList,
                barterList
        );
        // 거래내역을 조회
        Barter myBarter = barterRepository.findById(barterId).orElseThrow(() -> new IllegalArgumentException("barter not found"));
        // 바이어Id와 셀러Id에 유저아이디가 없을 경우
        if (!myBarter.getBuyerId().equals(userId) && !myBarter.getSellerId().equals(userId)) {throw new CustomException(NOT_FOUND_USER);}
        // 만약 바이어Id와 로그인 유저가 동일하면, 상대방의 아이디를 셀러Id로 식별
        if (myBarter.getBuyerId().equals(userId)) {
            Long oppenetId = myBarter.getSellerId();
            User oppentUser = userRepository.findById(oppenetId).orElseThrow(() -> new IllegalArgumentException("user not found"));

            // 상대방의 정보를 조회
            return new OppentScoreResponseDto(
                    oppenetId,
                    oppentUser.getNickname(),
                    myBarterList,
                    barterList
            );
        } else {
            // 만약 바이어Id와 로그인 유저Id가 다르다면, 상대방의 아이디를 바이어Id로 식별
            Long oppenetId = myBarter.getBuyerId();
            User oppentUser = userRepository.findById(oppenetId).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

            // 상대방의 정보를 조회
            return new OppentScoreResponseDto(
                    oppenetId,
                    oppentUser.getNickname(),
                    myBarterList,
                    barterList
            );
        }
    }
    // 리스트에 넣기
    private void splitAddList(Item buyerItem,
                              Long userId,
                              List<MyBarterScorDto> myBarterList,
                              MyBarterScorDto ItemList,
                              List<MyBarterScorDto> barterList) {
        if (buyerItem.getBag().getUserId().equals(userId)) {
            myBarterList.add(ItemList);
        } else {
            barterList.add(ItemList);
        }
    }
    // 아이디와 이미지를 넣어준다.
    private MyBarterScorDto getMyBarterScorDto(Long itemId, Item Item) {
        MyBarterScorDto buyerItemList = new MyBarterScorDto(
                itemId,
                Item.getItemImg()
        );
        return buyerItemList;
    }


    // 성훈 - 상대 평점주기
    @Transactional
    public BarterStatusDto gradeScore(GradeScoreRequestDto gradeScoreRequestDto, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        // 상대 userId
        Long opponentUserId = gradeScoreRequestDto.getUserId();
        // 평가주기
        float gradeScore = gradeScoreRequestDto.getScore();
        // 상대찾기
        User opponentUser = userRepository.findById(opponentUserId).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        // 등급
        String userDegree;
        String opponentDegree;
        //전체 점수
        float userTotalGrade = user.getTotalGrade();
        float opponentUserTotalGrade = opponentUser.getTotalGrade();
        // 상대 평가점수
        float opponentGrade = opponentUser.getGrade();
        // 상대 평가자 수
        int opponentRaterCnt = opponentUser.getRaterCount();

        // 거래내역 조회
        Long barterId = gradeScoreRequestDto.getBarterId();
        Barter barter = barterRepository.findById(barterId).orElseThrow(() -> new CustomException(NOT_FOUND_BARTER));

        // 이미 평가를 완료한 경우
        if (barter.getStatus() != 3) {
            throw new CustomException(FINISH_SCORE_BARTER);
            // 거래내역의 상대 Id와 Request로 전달받은 상대방의 정보와 다를 경우
        } else if ((!opponentUserId.equals(barter.getBuyerId())) && (!opponentUserId.equals(barter.getSellerId()))) {
            throw new CustomException(NOT_FOUND_USER);
            // 자기 자신에게 점수를 줄 경우
        } else if (opponentUserId.equals(user.getId())) {
            throw new CustomException(NOT_SCORE_MY_BARTER);
        }

        String[] barterIdList = barter.getBarter().split(";");
        String[] buyerItemId = barterIdList[0].split(",");
        String sellerItemId = barterIdList[1];

//        int viewBonusCntB = 0;
//        int viewBonusCntS = 0;
//        int scrabBonusCntB = 0;
//        int scrabBonusCntS = 0;
        int status = 4;
        // 내 포지션 확인
        String myPosition;
        // 상대가 바이어라면 -> 유저는 셀러이므로 셀러거래완료를 true로 변경한다.
        if (barter.getBuyerId().equals(opponentUserId)) {
            myPosition = "seller";
            if (barter.getIsSellerScore()) {
                throw new CustomException(FINISH_SCORE_BARTER);
            }
            // 바이어의 아이템 (여러개)
            for (String eachBuyer : buyerItemId) {
                Long buyerId = Long.valueOf(eachBuyer);
                Item buyerItem = itemRepository.findById(buyerId).orElseThrow(
                        () -> new CustomException(NOT_FOUND_SELLER_ITEM));

                // 아이템 업데이트
                buyerItem.statusUpdate(buyerItem.getId(), status);

                // 상대의 포지션이 바이어일 때, 조회수 100 이상이면 보너스 카운트 up!
//                if (buyerItem.getViewCnt() >= 100 && !myPosition.equals("buyer")) {
//                    viewBonusCntB++;
//                }
//                if (buyerItem.getViewCnt() >= 100 && !myPosition.equals("seller")) {
//                    viewBonusCntS++;
//                }
//                // 상대의 포지션이 바이어일 때, 찜하기가 10 이상이면 보너스 카운트 up!
//                if (buyerItem.getScrabCnt() >= 10 && !myPosition.equals("buyer")) {
//                    scrabBonusCntB++;
//                }
//                if (buyerItem.getScrabCnt() >= 10 && !myPosition.equals("seller")) {
//                    scrabBonusCntS++;
//                }
            }

            barter.updateScoreSeller(true);

        } else {
            myPosition = "buyer";
            if (barter.getIsBuyerScore()) {
                throw new CustomException(FINISH_SCORE_BARTER);
            }
            //셀러(유저)의 물품을 찾아서 정보를 넣기
            Long sellerId = Long.parseLong(sellerItemId);
            Item sellerItem = itemRepository.findById(sellerId).orElseThrow(
                    () -> new CustomException(NOT_FOUND_SELLER_ITEM)
            );

            sellerItem.statusUpdate(sellerItem.getId(), status);
            // 유저 정보를 업데이트 이후 status를 거래완료(3) -> 평가완료(4)으로 업데이트를 한다.

            // 좋은 물품 보너스 //
            // 상대의 포지션이 셀러일 때, 조회수 100 이상이면 보너스 카운트 up!
//            if (sellerItem.getViewCnt() >= 100 && !myPosition.equals("seller")) {
//                viewBonusCntS++;
//            }
//            if (sellerItem.getViewCnt() >= 100 && !myPosition.equals("buyer")) {
//                viewBonusCntB++;
//            }
//            // 상대의 포지션이 셀러일 때, 찜하기가 10 이상이면 보너스 카운트 up!
//            if (sellerItem.getScrabCnt() >= 10 && !myPosition.equals("seller")) {
//                scrabBonusCntS++;
//            }
//            if (sellerItem.getScrabCnt() >= 100 && !myPosition.equals("buyer")) {
//                scrabBonusCntB++;
//            }
            barter.updateScoreBuyer(true);
        }

//        // 응답 보너스 //
//        // 10분이네 응답 보너스
//        ChatRoom chatRoomCheck;
//        // 채팅방을 찾아보고, 없을 시 DB에 채팅방 저장
//        if (myPosition.equals("buyer")) {
//            chatRoomCheck = chatRoomRepository.findByRequesterAndAcceptor(user, opponentUser).orElseThrow(
//                    () -> new IllegalArgumentException("chatRoom not found"));
//        } else {
//            chatRoomCheck = chatRoomRepository.findByRequesterAndAcceptor(opponentUser, user).orElseThrow(
//                    () -> new IllegalArgumentException("chatRoom not found"));
//        }
//        Long chatRoomId = chatRoomCheck.getId();
//        Long myUserId = user.getId();
//
//        ChatMessage myMessage = chatMessageRepository.findFirstBySenderIdAndRoomId(myUserId, chatRoomId).orElseThrow(
//                () -> new IllegalArgumentException("chatRoom not found"));
//        ChatMessage opponentMessage = chatMessageRepository.findFirstBySenderIdAndRoomId(opponentUserId, chatRoomId).orElseThrow(
//                () -> new IllegalArgumentException("chatRoom not found"));
//
//        // 각자의 최초 메시지의 시간을 조회한다
//        LocalDateTime firstMyTime = myMessage.getCreatedAt();
//        LocalDateTime firstOppoentTime = opponentMessage.getCreatedAt();
//        long minute = ChronoUnit.MINUTES.between(firstMyTime, firstOppoentTime);
//        boolean chatTime = Math.abs(minute) <= 10;

        // 보너스 정산 //
//        if (myPosition.equals("buyer")) {
            // 좋은 물품 보너스 //
            // 완료된 거래내역의 조회수나 찜하기가 많으면 보너스
//            if (viewBonusCntB >= 1 || scrabBonusCntB >= 1) {
//                userTotalGrade = userTotalGrade + 1.0f * (viewBonusCntB + scrabBonusCntB);
//            }
//            if (viewBonusCntS >= 1 || scrabBonusCntS >= 1) {
//                opponentUserTotalGrade = opponentUserTotalGrade + 1.0f * (viewBonusCntS + scrabBonusCntS);
//            }
            // 10분 이내 응답했을 때의 보너스 -> 상데가 셀러이므로
//            if (!barter.getIsSellerScore()) {
//                if (chatTime) {
//                    opponentUserTotalGrade = opponentUserTotalGrade + 1.0f;
//                }
//            }

            //내 포지션에 셀러일 경우 / 상대는 바이어
//        } else {
//            if (viewBonusCntB >= 1 || scrabBonusCntB >= 1) {
//                opponentUserTotalGrade = opponentUserTotalGrade + 1.0f * (viewBonusCntB + scrabBonusCntB);
//            }
//            if (viewBonusCntS >= 1 || scrabBonusCntS >= 1) {
//                userTotalGrade = userTotalGrade + 1.0f * (viewBonusCntS + scrabBonusCntS);
//            }
            // 10분 이내 응답했을 때의 보너스 / 셀러에게 보너스
//            if (chatTime) {
//                // 상대가 응답을 안했을 경우 -> 응답 보너스의 중목을 막기위함
//                if (!barter.getIsBuyerScore()) {
//                    userTotalGrade = userTotalGrade + 1.0f;
//                }
//            }
//        }

        // 상대의 전체 점수에 1, 2은 - / 3은 0 / 4, 5은 +
        if (gradeScore <= 2) {
            opponentUserTotalGrade = opponentUserTotalGrade - (3.0f - gradeScore);
        } else if (gradeScore >= 4) {
            opponentUserTotalGrade = opponentUserTotalGrade + (gradeScore - 3.0f);
        }
        // 유저의 평균 평점
        if (opponentGrade == 0) {
            opponentGrade = gradeScore;
        } else {
            opponentGrade = (opponentGrade + gradeScore) / 2;
        }

        // 평가자수 +1
        opponentRaterCnt = opponentRaterCnt + 1;

        // 칭호 등급
        opponentDegree = updateDegree(opponentUserTotalGrade);
        userDegree = updateDegree(userTotalGrade);

        // 각자 평가한 점수를 업데이트를 해준다.
        opponentUser.updateFirstScore(
                opponentUserTotalGrade,
                opponentGrade,
                opponentRaterCnt,
                opponentDegree
        );

        // 상대 보너스 업데이트
        user.updateSecondScore(
                userTotalGrade,
                userDegree
        );

        Boolean buyerScore = barter.getIsBuyerScore();
        Boolean sellerScore = barter.getIsSellerScore();
        // 바이어와 셀러가 둘다 평가를 true로 하였다면 거래내역과 물품의 상태를 평가완료로 변경 ( status : 3 -> 4)
        if (buyerScore && sellerScore) {

            // 유저 정보를 업데이트 이후 status를 거래완료(3) -> 평가완료(4)으로 업데이트를 한다.
            barter.updateBarter(status);
            return new BarterStatusDto(true, true, barter.getStatus());
        }

        // 알림 내역 저장
        Notification notification = notificationRepository.save(Notification.createOfBarter(barter, user.getNickname(), myPosition, "Score"));
        // 상대방의 sup주소로 알람전송
        sendScoreMessage(barter, myPosition, notification);

        return new BarterStatusDto(true, true, barter.getStatus());
    }

    // 메시지 보내기
    private void sendScoreMessage(Barter barter, String myPosition, Notification notification) {
        if (myPosition.equals("buyer")){
            messagingTemplate.convertAndSend(
                    "/sub/notification/" + barter.getSellerId(), NotificationDto.createFrom(notification)
            );
        } else {
            messagingTemplate.convertAndSend(
                    "/sub/notification/" + barter.getBuyerId(), NotificationDto.createFrom(notification)
            );
        }
    }

    // 등급 단계
    private String updateDegree(float totalGrade) {
        String degree;
        // 칭호 등급
        if (totalGrade >= 150.0f) {
            degree = "물물박사";
        } else if (totalGrade >= 100.0f) {
            degree = "물물석사";
        } else if (totalGrade >= 70.0f) {
            degree = "물물학사";
        } else if (totalGrade >= 50.0f) {
            degree = "물물학생";
        } else {
            degree = "물물어린이";
        }
        return degree;
    }
}
