package com.sparta.mulmul.trade;

import com.sparta.mulmul.barter.BarterRepository;
import com.sparta.mulmul.barter.barterDto.BarterMessageDto;
import com.sparta.mulmul.barter.barterDto.BarterStatusDto;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.item.Item;
import com.sparta.mulmul.item.ItemRepository;
import com.sparta.mulmul.barter.Barter;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.trade.tradeDto.RequestTradeDto;
import com.sparta.mulmul.trade.tradeDto.TradeDecisionDto;
import com.sparta.mulmul.trade.tradeDto.TradeInfoDto;
import com.sparta.mulmul.trade.tradeDto.TradeInfoImagesDto;
import com.sparta.mulmul.user.BagRepository;
import com.sparta.mulmul.user.User;
import com.sparta.mulmul.user.UserRepository;
import com.sparta.mulmul.websocket.Notification;
import com.sparta.mulmul.websocket.NotificationRepository;
import com.sparta.mulmul.websocket.chatDto.NotificationDto;
import com.sparta.mulmul.websocket.chatDto.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sparta.mulmul.exception.ErrorCode.*;

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
                () -> new CustomException(NOT_FOUND_USER)
        );
        String sellerNickName = user.getNickname();

        List<Item> myItemList = itemRepository.findAllByBagId(myBadId);

        Item item = itemRepository.findById(itemid).orElseThrow(
                () -> new CustomException(NOT_FOUND_ITEM)
        );


        String sellerImages = item.getItemImg().split(",")[0];
        List<TradeInfoImagesDto> tradeInfoImagesDtoArrayList = new ArrayList<>();
        for (Item items : myItemList) {
            if (items.getStatus() == 1 || items.getStatus() == 0) {
                String itemImage = items.getItemImg().split(",")[0];
                Long itemId = items.getId();
                TradeInfoImagesDto tradeInfoImagesDto = new TradeInfoImagesDto(itemImage, itemId);
                tradeInfoImagesDtoArrayList.add(tradeInfoImagesDto);
            }
        }

        return new TradeInfoDto(sellerNickName, sellerImages, tradeInfoImagesDtoArrayList);
    }

    // 이승재 / 교환신청하기 누르면 아이템의 상태 변환 & 거래내역 생성
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "barterMyInfo", key = "#userDetails.userId", allEntries = true),
            @CacheEvict(cacheNames = "userProfile", key = "#userDetails.userId", allEntries = true),
            @CacheEvict(cacheNames = "itemInfo", allEntries = true),
            @CacheEvict(cacheNames = "itemDetailInfo", key = "#userDetails.userId + '::' + #requestTradeDto.itemId")})
    public String requestTrade(RequestTradeDto requestTradeDto, UserDetailsImpl userDetails) {
        // 아이템 상태 업데이트
        Item sellerItem = itemRepository.findById(requestTradeDto.getItemId()).orElseThrow(
                () -> new CustomException(NOT_FOUND_ITEM)
        );
        sellerItem.statusUpdate(sellerItem.getId(), 1);
        for (Long buyerItemIds : requestTradeDto.getMyItemIds()) {
            Item buyerItem = itemRepository.findById(buyerItemIds).orElseThrow(
                    () -> new CustomException(NOT_FOUND_ITEM)
            );
            buyerItem.statusUpdate(buyerItemIds, 2);
        }

        //Long 형태인 아이디들을 String 형태로 변환
        List<String> buyerItemIds = new ArrayList<>();
        for (Long itemId : requestTradeDto.getMyItemIds()) {
            buyerItemIds.add(itemId.toString());
        }

        //String barter 값 생성
        String StringbuyerItemIds = String.join(",", buyerItemIds);
        String[] barterList = new String[]{StringbuyerItemIds, requestTradeDto.getItemId().toString()};
        String StringBarter = String.join(";", barterList);
        Optional<Barter> findBarter = barterRepository.findByBarter(StringBarter);
        if (findBarter.isPresent()) {
            return "false";
        } else {
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
            User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
            Notification notification = notificationRepository.save(Notification.createOf(barter, user.getNickname()));
            // 리팩토링 필요
            messagingTemplate.convertAndSend(
                    "/sub/notification/" + requestTradeDto.getUserId(), NotificationDto.createFrom(notification)
            );
            return "true";
        }
    }


    // 이승재 교환신청 확인 페이지
    @Cacheable(cacheNames = "itemTradeCheckInfo", key = "#userDetails.userId")
    public TradeDecisionDto tradeDecision(Long barterId, UserDetailsImpl userDetails) {
        Barter barter = barterRepository.findById(barterId).orElseThrow(
                () -> new CustomException(NOT_FOUND_BARTER)
        );
        User buyer = userRepository.findById(barter.getBuyerId()).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER)
        );
        User seller = userRepository.findById(barter.getSellerId()).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER)
        );

        // 판매자의  닉네임 & degree
        String nickname = seller.getNickname();
        String degree = seller.getDegree();

        // 판매자 아이템 이미지 & 제목 & 내용
        Long sellerItemId = Long.valueOf(barter.getBarter().split(";")[1]);
        Item item = itemRepository.findById(sellerItemId).orElseThrow(
                () -> new CustomException(NOT_FOUND_ITEM)
        );
        String image = item.getItemImg().split(",")[0];
        String title = item.getTitle();
        String contents = item.getContents();

        // 구매자 아이템 이미지들
        String buyerItem = barter.getBarter().split(";")[0];
        List<Long> buyerItemId = new ArrayList<>();
        for (int i = 0; i < buyerItem.split(",").length; i++) {
            buyerItemId.add(Long.valueOf(buyerItem.split(",")[i]));
        }
        List<TradeInfoImagesDto> barterItem = new ArrayList<>();
        for (Long id : buyerItemId) {
            Item item1 = itemRepository.findById(id).orElseThrow(
                    () -> new CustomException(NOT_FOUND_ITEM)
            );
            String buyerItemImage = item1.getItemImg().split(",")[0];
            Long itemId = item1.getId();
            TradeInfoImagesDto tradeInfoImagesDto = new TradeInfoImagesDto(buyerItemImage, itemId);
            barterItem.add(tradeInfoImagesDto);
        }
        //구매자 닉네임
        String opponentNickname = buyer.getNickname();
        //거래 상태 확인
        String accepted;
        if (barter.getStatus() == 2) {
            accepted = "true";
        } else {
            accepted = "false";
        }
        return new TradeDecisionDto(opponentNickname, nickname, degree, title, contents, image, accepted, barterItem);
    }


    // 이승재 교환신청 확인 페이지 수락 버튼
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "barterMyInfo", key = "#userDetails.userId", allEntries = true),
            @CacheEvict(cacheNames = "userProfile", allEntries = true),
            @CacheEvict(cacheNames = "itemInfo", allEntries = true),
            @CacheEvict(cacheNames = "itemDetailInfo", allEntries = true),
            @CacheEvict(cacheNames = "itemTradeCheckInfo", allEntries = true)})
    public BarterStatusDto acceptTrade(Long barterId, UserDetailsImpl userDetails) {
        Barter barter = barterRepository.findById(barterId).orElseThrow(
                () -> new CustomException(NOT_FOUND_BARTER)
        );
        // 거래내역 상태 업데이트
        barter.updateBarter(2);

        //아이템 상태 업데이트
        Long sellerItemId = Long.valueOf(barter.getBarter().split(";")[1]);
        Item sellerItem = itemRepository.findById(sellerItemId).orElseThrow(
                () -> new CustomException(NOT_FOUND_ITEM)
        );
        sellerItem.statusUpdate(sellerItemId, 2);
        String buyerItem = barter.getBarter().split(";")[0];
        List<Long> buyerItemId = new ArrayList<>();
        for (int i = 0; i < buyerItem.split(",").length; i++) {
            buyerItemId.add(Long.valueOf(buyerItem.split(",")[i]));
        }
        for (Long id : buyerItemId) {
            Item item = itemRepository.findById(id).orElseThrow(
                    () -> new CustomException(NOT_FOUND_ITEM)
            );
            item.statusUpdate(id, 2);
        }
        Boolean isTrade = false;
        Boolean isScore = false;
        int status = 2;

        sendMyMessage(barterId, barter, userDetails.getUserId());
        return new BarterStatusDto(isTrade, isScore, status);

    }

    // 내게 거래완료 정보 메시지 보내기
    private void sendMyMessage(Long barterId, Barter barter, Long userId) {
        // 나의 sup주소로 전송
        if (barter.getBuyerId().equals(userId)) {
            // 내게 보낼 메시지 정보 담기
            BarterMessageDto messageDto = new BarterMessageDto(
                    barterId,
                    false,
                    barter.getStatus(),
                    "seller"
            );
            messagingTemplate.convertAndSend(
                    "/sub/barter/" + barter.getSellerId(), messageDto
            );
        } else {
            // 내게 보낼 메시지 정보 담기
            BarterMessageDto messageDto = new BarterMessageDto(
                    barterId,
                    false,
                    barter.getStatus(),
                    "buyer"
            );
            messagingTemplate.convertAndSend(
                    "/sub/barter/" + barter.getBuyerId(), messageDto
            );
        }
    }


    //  교환신청 확인페이지 거절버튼
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "barterMyInfo", allEntries = true),
            @CacheEvict(cacheNames = "itemInfo", allEntries = true),
            @CacheEvict(cacheNames = "itemDetailInfo", allEntries = true),
            @CacheEvict(cacheNames = "itemTradeCheckInfo", allEntries = true)})
    public void deleteTrade(Long barterId) {
        //아이템 상태 업데이트
        Barter barter = barterRepository.findById(barterId).orElseThrow(
                () -> new CustomException(NOT_FOUND_BARTER)
        );
        Long sellerItemId = Long.valueOf(barter.getBarter().split(";")[1]);
        Item sellerItem = itemRepository.findById(sellerItemId).orElseThrow(
                () -> new CustomException(NOT_FOUND_ITEM)
        );
        sellerItem.statusUpdate(sellerItemId, 0);
        String buyerItem = barter.getBarter().split(";")[0];
        List<Long> buyerItemId = new ArrayList<>();
        for (int i = 0; i < buyerItem.split(",").length; i++) {
            buyerItemId.add(Long.valueOf(buyerItem.split(",")[i]));
        }
        for (Long id : buyerItemId) {
            Item item = itemRepository.findById(id).orElseThrow(
                    () -> new CustomException(NOT_FOUND_ITEM)
            );
            item.statusUpdate(id, 0);
        }

        // 거래내역 삭제
        barterRepository.deleteById(barterId);
        // 알림에서 제거
        notificationRepository.deleteByChangeIdAndType(barterId, NotificationType.BARTER);
    }
}
