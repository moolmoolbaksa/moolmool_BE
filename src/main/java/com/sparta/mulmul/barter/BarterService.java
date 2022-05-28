package com.sparta.mulmul.barter;

import com.sparta.mulmul.barter.barterDto.*;
import com.sparta.mulmul.model.Barter;
import com.sparta.mulmul.websocket.chatDto.NotificationDto;
import com.sparta.mulmul.websocket.chatDto.NotificationType;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.item.Item;
import com.sparta.mulmul.websocket.Notification;
import com.sparta.mulmul.user.User;
import com.sparta.mulmul.item.ItemRepository;
import com.sparta.mulmul.websocket.NotificationRepository;
import com.sparta.mulmul.user.UserRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.sparta.mulmul.exception.ErrorCode.*;

@CacheConfig
@RequiredArgsConstructor
@Service
public class BarterService {
    private final BarterRepository barterRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final NotificationRepository notificationRepository;


    // 성훈 - 거래내역서 보기
//    @Cacheable(cacheNames = "barterMyInfo", key = "#userDetails.userId")
    public List<BarterDto> showMyBarter(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER)
        );

        Long userId = userDetails.getUserId();
        // 유저의 거래내역 리스트를 전부 조회한다
        List<Barter> mybarterList = barterRepository.findAllByBuyerIdOrSellerId(userId, userId);
        // 거래내역 리스트를 담기
        List<BarterDto> totalList = addTotalList(userId, mybarterList);
        return totalList;
    }


    // 엄성훈 - 거래완료취소 유저의 isTrade를 true -> false 업데이트
    @Transactional
    public BarterTradeCheckDto cancelBarter(Long barterId, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        Barter mybarter = barterRepository.findById(barterId).orElseThrow(() -> new CustomException(NOT_FOUND_BARTER));
        BarterTradeCheckDto oppononetTreadeCheck;
        Long userId = user.getId();
        // 거래완료 취소 업데이트
        if (mybarter.getStatus() == 2) {
            oppononetTreadeCheck = getBarterTradeCheckDto(userId, mybarter);
        } else {
            // 거래중인 상태가 아니면 예외처리
            throw new CustomException(NOT_TRADE_BARTER);
        }
        return oppononetTreadeCheck;
    }


    // 교환신청 취소 물건상태 2(교환중) -> 0(물품등록한 상태), 거래내역 삭제
    @Transactional
    public void deleteBarter(Long barterId, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        Long userId = user.getId();
        Barter mybarter = barterRepository.findById(barterId).orElseThrow(() -> new CustomException(NOT_FOUND_BARTER));
        // 거래중인 상태가 아니면 예외처리
        if (mybarter.getStatus() == 1 || mybarter.getStatus() == 2) {
            // 거래외 사람이 취소를 할 경우
            if (!mybarter.getBuyerId().equals(userId) && !mybarter.getSellerId().equals(userId)) {
                throw new CustomException(NOT_FOUND_BARTER);
            }

            String[] barterIdList = mybarter.getBarter().split(";");
            String[] buyerItemId = barterIdList[0].split(",");
            String sellerItemId = barterIdList[1];

            // 아이템 상태 업데이트
            updateStatus(buyerItemId, sellerItemId);
            // 거래내역 삭제
            barterRepository.deleteById(barterId);
            notificationRepository.deleteByChangeIdAndType(barterId, NotificationType.BARTER);
        } else {
            throw new CustomException(NOT_FOUND_BARTER);
        }
    }

    // 성훈 - 거래 완료
    @Transactional
    public BarterStatusDto okayBarter(Long barterId, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        // 거래내역을 조회한다.
        Barter myBarter = barterRepository.findById(barterId).orElseThrow(() -> new CustomException(NOT_FOUND_BARTER));
        // 거래중인 상태가 아니면 예외처리
        if (myBarter.getStatus() != 2) {
            throw new CustomException(NOT_TRADE_BARTER);
        }

        Long userId = user.getId();
        boolean opponentTrade;
        String myPosition;
        // 유저가 바이어라면 바이어거래완료를 true로 변경한다.
        if (myBarter.getBuyerId().equals(userId)) {
            if (myBarter.getIsBuyerTrade()) {
                throw new CustomException(FINISH_BARTER);
            }
            myBarter.updateTradBuyer(true);
            // 상대방의 거래유무
            opponentTrade = myBarter.getIsSellerTrade();
            myPosition = "buyer";
            // 유저가 셀러라면 셀러거래완료를 true로 변경한다.
        } else {
            if (myBarter.getIsSellerTrade()) {
                throw new CustomException(FINISH_BARTER);
            }
            myBarter.updateTradSeller(true);
            opponentTrade = myBarter.getIsBuyerTrade();
            myPosition = "seller";
        }

        Boolean buyerTrade = myBarter.getIsBuyerTrade();
        Boolean sellerTrade = myBarter.getIsSellerTrade();

        // 바이어와 셀러 모두 거래완료이면 (거래내역과 아이템)의 상태를 거래완료(status : 3)으로 변경
        return checkIsTrade(barterId, user, myBarter, opponentTrade, myPosition, buyerTrade, sellerTrade);
    }


    // 거래내역 수정하기
    @Transactional
    public void editBarter(EditRequestDto editRequestDto, UserDetailsImpl userDetails) {
        // 수정할 거래내역 찾기
        Barter barter = barterRepository.findById(editRequestDto.getBarterId()).orElseThrow(() -> new CustomException(NOT_FOUND_BARTER));
        // 유저
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        String barterItems = barter.getBarter();
        String[] buyerItemList = barterItems.split(";")[0].split(",");
        String sellerItem = barterItems.split(";")[1];

        // 수정하기 전 아이템의 buyer의 아이템 상태를 0으로 초기화 해준다.
        for (String eachItem : buyerItemList) {
            Long eachItemId = Long.parseLong(eachItem);
            Item eachItems = itemRepository.findById(eachItemId).orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));
            eachItems.statusUpdate(eachItemId, 0);
        }

        String editItemIds = null;
        // 수정할 아이템의 아이템 상태를 거래중 (2)로 만들어준다.
        for (Long eachEditItemId : editRequestDto.getItemId()) {
            Item editItems = itemRepository.findById(eachEditItemId).orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));
            editItems.statusUpdate(eachEditItemId, 2);
            // 아이템의 아이디를 String형태로 변환하여 edit
            if (editItemIds != null) {
                editItemIds = editItemIds + "," + eachEditItemId;
            } else {
                editItemIds = String.valueOf(eachEditItemId);
            }
        }
        // 수정된 거래사항을 업데이트합니다.
        String editBarter = editItemIds + ";" + sellerItem;
        barter.editBarter(editBarter);
    }


    // 한명이 거래완료를 하였는지, 두명 다 거래를 완료했는지 판단
    private BarterStatusDto checkIsTrade(Long barterId, User user, Barter myBarter, boolean opponentTrade, String myPosition, Boolean buyerTrade, Boolean sellerTrade) {
        if (buyerTrade && sellerTrade) {

            String[] barterIdList = myBarter.getBarter().split(";");
            String[] buyerItemId = barterIdList[0].split(",");
            String sellerItemId = barterIdList[1];

            // 아이템 & 거래내역 업데이트
            updateStatus(myBarter, buyerItemId, sellerItemId);
            Long sellerId = myBarter.getSellerId();

            List<Barter> sellerBarterList = barterRepository.findAllBySellerId(sellerId);
            for (Barter eachBarter : sellerBarterList) {

                Long eachBarterId = eachBarter.getId();
                String[] eachBarterIdList = eachBarter.getBarter().split(";");
                String[] eachBuyerItemId = eachBarterIdList[0].split(",");
                String eachSellerItemId = eachBarterIdList[1];

                // 해당 거래내역이 아닌, 동일한 셀러아이템일 경우
                if (!eachBarterId.equals(barterId) && eachSellerItemId.equals(sellerItemId)) {
                    for (String eachBuyerItem : eachBuyerItemId) {
                        Long buyerItemIds = Long.parseLong(eachBuyerItem);
                        Item eachBuyerItems = itemRepository.findById(buyerItemIds).orElseThrow(() -> new CustomException(NOT_FOUND_BUYER_ITEM));
                        // 다른 바이어 아이템들을 0으로 초기화해준다.
                        eachBuyerItems.statusUpdate(buyerItemIds, 0);
                        // 거래내역을 삭제한다.
                        barterRepository.delete(eachBarter);
                    }
                }
            }

            // 내게 거래완료 메시지 보내기
            sendMyMessage(barterId, myBarter, myPosition);
            return new BarterStatusDto(opponentTrade, false, myBarter.getStatus());
        } else {
            // 알림 내역 저장
            Notification notification = notificationRepository.save(Notification.createOfBarter(myBarter, user.getNickname(), myPosition, "Barter"));
            // 상대방의 sup주소로 전송
            sendTradeMessage(myBarter, myPosition, notification);
            // 내게 거래완료 메시지 보내기
            sendMyMessage(barterId, myBarter, myPosition);
            return new BarterStatusDto(opponentTrade, false, myBarter.getStatus());
        }
    }


    // 거래내역 리스트를 담기
    private List<BarterDto> addTotalList(Long userId, List<Barter> mybarterList) {

        // 나의 거래완료 여부
        Boolean opponentTradeCheck;
        Boolean myScoreCheck;
        Boolean myTradeCheck;
        List<BarterDto> totalList = new ArrayList<>();

        for (Barter barters : mybarterList) {
            Long barterId = barters.getId();
            LocalDateTime date = barters.getModifiedAt();

            String barter = barters.getBarter();
            //barter 거래내역 id split하기 -> 파싱하여 거래항 물품의 Id값을 찾기
            String[] barterIds = barter.split(";");
            String[] buyerItemIdList = barterIds[0].split(",");
            String[] sellerItemIdList = barterIds[1].split(",");


            // 거래상태 정보 1 : 신청중 / 2 : 거래중 / 3 : 거래완료 / 4 : 평가완료
            int status = barters.getStatus();
            Long opponentId;
            String myPosition;
            //내포지션이 바이어라면 거래내역의 상태 확인하기
            if (barters.getBuyerId().equals(userId)) {
                myTradeCheck = barters.getIsBuyerTrade();
                myScoreCheck = barters.getIsBuyerScore();
                opponentTradeCheck = barters.getIsSellerTrade();
                myPosition = "buyer";
                opponentId = barters.getSellerId();
                //내포지션이 셀러라면 거래내역의 상태 확인하기
            } else {
                myTradeCheck = barters.getIsSellerTrade();
                myScoreCheck = barters.getIsSellerScore();
                opponentTradeCheck = barters.getIsBuyerTrade();
                myPosition = "seller";
                opponentId = barters.getBuyerId();
            }
            // 상대 유저 정보
            User opponentUser = userRepository.findById(opponentId).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

            // 거래 물품리스트를 담을 Dto -> 내것과 상대것을 담는다
            List<OpponentBarterDto> myBarterList = new ArrayList<>();
            List<OpponentBarterDto> barterList = new ArrayList<>();
            // 바이어(유저)의 물품을 찾아서 정보를 넣기
            for (String buyerItemId : buyerItemIdList) {
                Long itemId = Long.parseLong(buyerItemId);
                BarterItemListDto buyerItem = itemRepository.findByBarterItems(itemId);
                // 각 아이템의 정보를 리스트에 담기
                OpponentBarterDto buyerItemList = getMyBarterDto(buyerItem);
                //상대와 나의 바터리스트에 각각 아이템을 넣기
                BarterCheckAddList(barters, userId, myBarterList, buyerItemList, barterList);
            }

            //셀러(유저)의 물품을 찾아서 정보를 넣기
            for (String sellerItemId : sellerItemIdList) {
                Long itemId = Long.parseLong(sellerItemId);
                BarterItemListDto sellerItem = itemRepository.findByBarterItems(itemId);
                // 각 아이템의 정보를 리스트에 담기
                OpponentBarterDto sellerItemList = getMyBarterDto(sellerItem);
                //상대와 나의 바터리스트에 각각 아이템을 담기
                BarterCheckAddList(barters, opponentId, myBarterList, sellerItemList, barterList);
            }

            // 성훈 리팩토링 (거래리스트 정보넣기 )
            BarterDto barterFin = new BarterDto(
                    barterId,
                    opponentId,
                    opponentUser.getNickname(),
                    opponentUser.getProfile(),
                    date,
                    status,
                    myPosition,
                    myTradeCheck,
                    myScoreCheck,
                    opponentTradeCheck,
                    myBarterList,
                    barterList
            );
            totalList.add(barterFin);
        }
        return totalList;
    }

    // 성훈 - 상대와 나의 바터리스트에 각각 아이템 정보를 넣기
    private void BarterCheckAddList(Barter barters, Long userId, List<OpponentBarterDto> myBarterList, OpponentBarterDto ItemList, List<OpponentBarterDto> barterList) {
        if (barters.getBuyerId().equals(userId)) {
            myBarterList.add(ItemList);
        } else {
            barterList.add(ItemList);
        }
    }

    // 성훈 리팩토링 (거래리스트)
    private OpponentBarterDto getMyBarterDto(BarterItemListDto Item) {
        return new OpponentBarterDto(
                Item.getItemId(),
                Item.getTitle(),
                Item.getItemImg().split(",")[0],
                Item.getContents()
        );
    }


    // 거래완료 취소 업데이트
    private BarterTradeCheckDto getBarterTradeCheckDto(Long userId, Barter mybarter) {
        BarterTradeCheckDto oppononetTreadeCheck;
        if (mybarter.getBuyerId().equals(userId)) {
            Boolean isTradeCheck = mybarter.getIsBuyerTrade();
            isTradeCheck(isTradeCheck);
            mybarter.updateTradBuyer(false);
            oppononetTreadeCheck = new BarterTradeCheckDto(mybarter.getIsSellerTrade());
        } else {
            Boolean isTradeCheck = mybarter.getIsSellerTrade();
            isTradeCheck(isTradeCheck);
            mybarter.updateTradSeller(false);
            oppononetTreadeCheck = new BarterTradeCheckDto(mybarter.getIsBuyerTrade());
        }
        return oppononetTreadeCheck;
    }

    // 거래완료를 하지 않았을 경우
    private void isTradeCheck(Boolean isTradeCheck) {
        if (isTradeCheck.equals(false)) {
            throw new CustomException(NOT_TRADE_COMPLETE_BARTER);
        }
    }


    // 아이템 & 거래내역 업데이트
    private void updateStatus(Barter myBarter, String[] buyerItemId, String sellerItemId) {
        for (String eachBuyer : buyerItemId) {
            Long buyerId = Long.valueOf(eachBuyer);
            Item buyerItem = itemRepository.findById(buyerId).orElseThrow(() -> new CustomException(NOT_FOUND_BUYER_ITEM));
            buyerItem.statusUpdate(buyerItem.getId(), 3);
        }
        //셀러(유저)의 물품을 찾아서 정보를 넣기
        Long sellerId = Long.parseLong(sellerItemId);
        Item sellerItem = itemRepository.findById(sellerId).orElseThrow(() -> new CustomException(NOT_FOUND_SELLER_ITEM));
        sellerItem.statusUpdate(sellerItem.getId(), 3);
        myBarter.updateTradeBarter(3, LocalDateTime.now());
    }


    // 상대 sup주소로 메시지 보내기 (상대가 거래완료를 누르지 않았을 경우) 리팩토링
    private void sendTradeMessage(Barter myBarter, String myPosition, Notification notification) {

        if (myPosition.equals("buyer")) {
            messagingTemplate.convertAndSend(
                    "/sub/notification/" + myBarter.getSellerId(), NotificationDto.createFrom(notification));
        } else {
            messagingTemplate.convertAndSend(
                    "/sub/notification/" + myBarter.getBuyerId(), NotificationDto.createFrom(notification));
        }
    }

    // 내게 거래완료 정보 메시지 보내기 리팩토링
    private void sendMyMessage(Long barterId, Barter myBarter, String myPosition) {
        // 나의 sup주소로 전송
        if (myPosition.equals("buyer")) {
            // 내게 보낼 메시지 정보 담기
            BarterMessageDto messageDto = new BarterMessageDto(
                    barterId,
                    myBarter.getIsBuyerTrade(),
                    myBarter.getStatus(),
                    "seller"
            );
            messagingTemplate.convertAndSend(
                    "/sub/barter/" + myBarter.getSellerId(), messageDto
            );
        } else {
            // 내게 보낼 메시지 정보 담기
            BarterMessageDto messageDto = new BarterMessageDto(
                    barterId,
                    myBarter.getIsSellerTrade(),
                    myBarter.getStatus(),
                    "buyer"
            );
            messagingTemplate.convertAndSend(
                    "/sub/barter/" + myBarter.getBuyerId(), messageDto
            );
        }
    }

    // 아이템 상태 업데이트
    private void updateStatus(String[] buyerItemId, String sellerItemId) {
        int setStatus = 0;
        for (String eachBuyer : buyerItemId) {
            Long buyerId = Long.valueOf(eachBuyer);
            Item buyerItem = itemRepository.findById(buyerId).orElseThrow(() -> new CustomException(NOT_FOUND_BUYER_ITEM));
            buyerItem.statusUpdate(buyerItem.getId(), setStatus);
        }
        //셀러(유저)의 물품을 찾아서 정보를 넣기
        Long sellerId = Long.parseLong(sellerItemId);
        Item sellerItem = itemRepository.findById(sellerId).orElseThrow(() -> new CustomException(NOT_FOUND_SELLER_ITEM));
        sellerItem.statusUpdate(sellerItem.getId(), setStatus);
    }


}