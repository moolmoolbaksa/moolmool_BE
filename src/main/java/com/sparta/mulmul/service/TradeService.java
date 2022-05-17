package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.BarterStatusDto;
import com.sparta.mulmul.dto.NotificationDto;
import com.sparta.mulmul.dto.NotificationType;
import com.sparta.mulmul.dto.trade.RequestTradeDto;
import com.sparta.mulmul.dto.trade.TradeDecisionDto;
import com.sparta.mulmul.dto.trade.TradeInfoDto;
import com.sparta.mulmul.dto.trade.TradeInfoImagesDto;
import com.sparta.mulmul.model.Barter;
import com.sparta.mulmul.model.Item;
import com.sparta.mulmul.model.Notification;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.*;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final BagRepository bagRepository;
    private final ItemRepository itemRepository;
    private final BarterRepository barterRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    // 이승재 / 교환신청하기 전 정보
    public TradeInfoDto showTradeInfo(Long itemid, Long userId, UserDetailsImpl userDetails) {
        Long myBadId = bagRepository.findByUserId(userDetails.getUserId()).getId();

        User user = userRepository.findById(userId).orElseThrow(
                ()-> new IllegalArgumentException("유저 정보가 없습니다.")
        );
        String sellerNickName = user.getNickname();

        List<Item> myItemList = itemRepository.findAllByBagId(myBadId);

        Item item = itemRepository.findById(itemid).orElseThrow(
                () -> new IllegalArgumentException("아이템이 없습니다.")
        );


        String sellerImages = item.getItemImg().split(",")[0];
        List<TradeInfoImagesDto> tradeInfoImagesDtoArrayList = new ArrayList<>();
        for(Item items : myItemList){
            if(items.getStatus()==1 || items.getStatus()==0) {
                String itemImage = items.getItemImg().split(",")[0];
                Long itemId = items.getId();
                TradeInfoImagesDto tradeInfoImagesDto = new TradeInfoImagesDto(itemImage, itemId);
                tradeInfoImagesDtoArrayList.add(tradeInfoImagesDto);
            }
        }

        return new TradeInfoDto(sellerNickName, sellerImages,  tradeInfoImagesDtoArrayList);
    }

    // 이승재 / 교환신청하기 누르면 아이템의 상태 변환 & 거래내역 생성
    @Transactional
    public void requestTrade(RequestTradeDto requestTradeDto, UserDetailsImpl userDetails) {
        // 아이템 상태 업데이트
        Item sellerItem = itemRepository.findById(requestTradeDto.getItemId()).orElseThrow(
                ()-> new IllegalArgumentException("아이템이 없습니다.")
        );
        sellerItem.statusUpdate(sellerItem.getId(), 1);
        for(Long buyerItemIds : requestTradeDto.getMyItemIds()) {
            Item buyerItem =  itemRepository.findById(buyerItemIds).orElseThrow(
                    ()-> new IllegalArgumentException("아이템이 없습니다.")
            );
            buyerItem.statusUpdate(buyerItemIds, 2);
        }

        //Long 형태인 아이디들을 String 형태로 변환
        List<String> buyerItemIds = new ArrayList<>();
        for(Long itemId : requestTradeDto.getMyItemIds()) {
            buyerItemIds.add(itemId.toString());
        }

        //String barter 값 생성
        String StringbuyerItemIds = String.join(",", buyerItemIds);
        String[] barterList = new String[]{StringbuyerItemIds, requestTradeDto.getItemId().toString()};
        String StringBarter = String.join(";", barterList);
        // 거래 내역 생성
        Barter barter = Barter.builder()
                .buyerId(userDetails.getUserId())
                .sellerId(requestTradeDto.getUserId())
                .barter(StringBarter)
                .status(1)
                .isBuyerScore(false)
                .isBuyerTrade(false)
                .isSellerScore(false)
                .isSellerTrade(false)
                .build();
        barterRepository.save(barter);
        // 알림 내역 저장 후 상대방에게 전송
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(()->new NullPointerException("해당 회원이 존재하지 않습니다."));
        Notification notification = notificationRepository.save(Notification.createOf(barter, user.getNickname()));
        // 리팩토링 필요
        messagingTemplate.convertAndSend(
                "/sub/notification/" + requestTradeDto.getUserId(), NotificationDto.createFrom(notification)
        );
    }


    // 이승재 교환신청 확인 페이지
    public TradeDecisionDto tradeDecision(Long baterId, UserDetailsImpl userDetails) {
        Barter barter = barterRepository.findById(baterId).orElseThrow(
                ()-> new IllegalArgumentException("거래내역이 없습니다.")
        );
        User buyer = userRepository.findById(barter.getBuyerId()).orElseThrow(
                ()-> new IllegalArgumentException("유저정보가 없습니다.")
        );
        User seller = userRepository.findById(barter.getSellerId()).orElseThrow(
                ()-> new IllegalArgumentException("유저정보가 없습니다.")
        );

        // 판매자의  닉네임 & degree
        String nickname = seller.getNickname();
        String degree = seller.getDegree();

        // 판매자 아이템 이미지 & 제목 & 내용
        Long sellerItemId = Long.valueOf(barter.getBarter().split(";")[1]);
        Item item = itemRepository.findById(sellerItemId).orElseThrow(
                ()-> new IllegalArgumentException("아이템이 없습니다.")
        );
        String image = item.getItemImg().split(",")[0];
        String title = item.getTitle();
        String contents = item.getContents();

        // 구매자 아이템 이미지들
        String buyerItem = barter.getBarter().split(";")[0];
        List<Long> buyerItemId = new ArrayList<>();
        for(int i = 0; i<buyerItem.split(",").length; i++){
            buyerItemId.add(Long.valueOf(buyerItem.split(",")[i]));
        }
        List<TradeInfoImagesDto> barterItem = new ArrayList<>();
        for(Long id : buyerItemId){
            Item item1 = itemRepository.findById(id).orElseThrow(
                    ()-> new IllegalArgumentException("아이템이 없습니다.")
            );
            String buyerItemImage = item1.getItemImg().split(",")[0];
            Long itemId = item1.getId();
            TradeInfoImagesDto tradeInfoImagesDto = new TradeInfoImagesDto(buyerItemImage, itemId);
            barterItem.add(tradeInfoImagesDto);
        }
        //구매자 닉네임
        String opponentNickname = buyer.getNickname();
        return new TradeDecisionDto(opponentNickname, nickname, degree, title, contents, image, barterItem);
    }


    // 이승재 교환신청 확인 페이지 수락 버튼
    @Transactional
    public BarterStatusDto acceptTrade(Long baterId) {
        Barter barter = barterRepository.findById(baterId).orElseThrow(
                ()-> new IllegalArgumentException("거래내역이 없습니다.")
        );
        // 거래내역 상태 업데이트
        barter.updateBarter(2);

        //아이템 상태 업데이트
        Long sellerItemId = Long.valueOf(barter.getBarter().split(";")[1]);
        Item sellerItem = itemRepository.findById(sellerItemId).orElseThrow(
                ()-> new IllegalArgumentException("아이템이 없습니다.")
        );
        sellerItem.statusUpdate(sellerItemId, 2);
        String buyerItem = barter.getBarter().split(";")[0];
        List<Long> buyerItemId = new ArrayList<>();
        for(int i = 0; i<buyerItem.split(",").length; i++){
            buyerItemId.add(Long.valueOf(buyerItem.split(",")[i]));
        }
        for(Long id : buyerItemId){
            Item item = itemRepository.findById(id).orElseThrow(
                    ()-> new IllegalArgumentException("아이템이 없습니다.")
            );
            item.statusUpdate(id,2);
        }
        Boolean isTrade = false;
        Boolean isScore = false;
        int status = 2;
        return new BarterStatusDto(isTrade, isScore, status);

    }


    //  교환신청 확인페이지 거절버튼
    @Transactional
    public void deleteTrade(Long baterId) {
        //아이템 상태 업데이트
        Barter barter = barterRepository.findById(baterId).orElseThrow(
                ()-> new IllegalArgumentException("거래내역이 없습니다.")
        );
        Long sellerItemId = Long.valueOf(barter.getBarter().split(";")[1]);
        Item sellerItem = itemRepository.findById(sellerItemId).orElseThrow(
                ()-> new IllegalArgumentException("아이템이 없습니다.")
        );
        sellerItem.statusUpdate(sellerItemId,0);
        String buyerItem = barter.getBarter().split(";")[0];
        List<Long> buyerItemId = new ArrayList<>();
        for(int i = 0; i<buyerItem.split(",").length; i++){
            buyerItemId.add(Long.valueOf(buyerItem.split(",")[i]));
        }
        for(Long id : buyerItemId){
            Item item = itemRepository.findById(id).orElseThrow(
                    ()-> new IllegalArgumentException("아이템이 없습니다.")
            );
            item.statusUpdate(id,0);
        }

        // 거래내역 삭제
        barterRepository.deleteById(baterId);
        // 알림에서 제거
        notificationRepository.deleteByChangeIdAndType(baterId, NotificationType.BARTER);
    }
}
