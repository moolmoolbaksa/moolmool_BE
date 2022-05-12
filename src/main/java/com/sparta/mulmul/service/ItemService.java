package com.sparta.mulmul.service;


import com.sparta.mulmul.dto.*;
import com.sparta.mulmul.dto.detailPageDto.DetailPageBagDto;
import com.sparta.mulmul.model.*;
import com.sparta.mulmul.repository.*;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final BagRepository bagRepositroy;
    private final ScrabRepository scrabRepository;
    private final UserRepository userRepository;
    private final BarterRepository barterRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final NotificationRepository notificationRepository;

    // 이승재 / 보따리 아이템 등록하기
    public void createItem(ItemRequestDto itemRequestDto, UserDetailsImpl userDetails){
        List<String> imgUrlList = itemRequestDto.getImgUrl();
        List<String> favoredList = itemRequestDto.getFavored();
        String imgUrl = String.join(",", imgUrlList);
        String favored = String.join(",", favoredList);


        // 유저 아이디를 통해 보따리 정보를 가져오고 후에 아이템을 저장할때 보따리 정보 넣어주기 & 아이템 개수 +1
        Bag bag = bagRepositroy.findByUserId(userDetails.getUserId());
         bag.update(bag.getItemCnt()+1);


        Item item = Item.builder()
                .title(itemRequestDto.getTitle())
                .contents(itemRequestDto.getContents())
                .address("서울")  //유저 정보에서 가져오기
                .category(itemRequestDto.getCategory())
                .scrabCnt(0)
                .commentCnt(0) // 사용할지 안할지 확정안됨
                .viewCnt(0)
                .status(0)
                .itemImg(imgUrl)
                .type(itemRequestDto.getType())
                .favored(favored)
                .bag(bag)
                .build();

        itemRepository.save(item);

    }
    //이승재 / 전체 아이템 조회(카테고리별)
    public List<ItemResponseDto> getItems(String category, UserDetailsImpl userDetails) {
        if(category.isEmpty()){
            List<Item> itemList = itemRepository.findAllByOrderByCreatedAtDesc();
            List<ItemResponseDto> items = new ArrayList<>();
            for(Item item : itemList){
                List<Scrab> scrabs = scrabRepository.findAllByItemId(item.getId());
                int scrabCnt = 0;
                for(Scrab scrab1 : scrabs){
                    if(scrab1.getScrab().equals(true)){
                        scrabCnt++;
                    }
                }
                ItemResponseDto itemResponseDto = new ItemResponseDto(
                        item.getId(),
                        item.getCategory(),
                        item.getTitle(),
                        item.getContents(),
                        item.getItemImg().split(",")[0],
                        item.getAddress(),
                        scrabCnt,
                        item.getViewCnt(),
                        item.getStatus());
                items.add(itemResponseDto);
                }
            return items;
            }
       List<Item> itemList = itemRepository.findAllByCategory(category);
       List<ItemResponseDto> items = new ArrayList<>();
       Long userId = userDetails.getUserId();
       for(Item item : itemList) {
           boolean isScrab;
           if(scrabRepository.findByUserIdAndItemId(userId, item.getId()).isPresent()){
               isScrab  = true;
           }else{
               isScrab = false;
           }
           List<Scrab> scrabs = scrabRepository.findAllByItemId(item.getId());
           int scrabCnt = 0;
           for(Scrab scrab1 : scrabs){
               if(scrab1.getScrab().equals(true)){
                   scrabCnt++;
               }
           }
           ItemResponseDto itemResponseDto = new ItemResponseDto(
                   item.getId(),
                   item.getCategory(),
                   item.getTitle(),
                   item.getContents(),
                   item.getItemImg().split(",")[0],
                   item.getAddress(),
                   scrabCnt,
                   item.getViewCnt(),
                   item.getStatus(),
                   isScrab);
           items.add(itemResponseDto);

       }
       return items;
    }


    // 이승재 / 아이템 상세페이지
    @Transactional
    public ItemDetailResponseDto getItemDetail(Long itemId, UserDetailsImpl userDetails) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                ()-> new IllegalArgumentException("아이템이 없습니다.")
        );


        // 아이템에 해당하는 보따리에 담겨있는 모든 아이템 이미지 가져오기
        List<Item> userItemList = itemRepository.findAllByBagId(item.getBag().getId());
        List<DetailPageBagDto> bagInfos = new ArrayList<>();
        for(Item item1 : userItemList){
            if(item1.getId()!=itemId) {
                String bagImg = item1.getItemImg().split(",")[0];
                Long bagItemId = item1.getId();
                DetailPageBagDto detailPageBagDto = new DetailPageBagDto(bagImg, bagItemId);
                bagInfos.add(detailPageBagDto);
            }
        }
        // 이승재 / 아이템 조회수 계산
        int viewCnt = item.getViewCnt();
        viewCnt += 1;
        item.update(itemId, viewCnt);

        // 이승재 / 아이템 구독 정보 유저 정보를 통해 확인
        Boolean isSrab = false;
        Optional<Scrab> scrab = scrabRepository.findByUserIdAndItemId(userDetails.getUserId(), itemId);
        if(scrab.isPresent()){
            if(scrab.get().getScrab().equals(true)){
                isSrab = true;
            }else{
                isSrab = false;
            }
        }

        User user = userRepository.findById(item.getBag().getUserId()).orElseThrow(
                ()-> new IllegalArgumentException("유저 정보가 없습니다.")
        );

        List<String> itemImgList = new ArrayList<>();
        for(int i = 0; i<item.getItemImg().split(",").length; i++){
            itemImgList.add(item.getItemImg().split(",")[i]);
        }
        List<Scrab> scrabs = scrabRepository.findAllByItemId(itemId);
        int scrabCnt = 0;
       for(Scrab scrab1 : scrabs){
           if(scrab1.getScrab().equals(true)){
               scrabCnt++;
           }
       }

        item.scrabCntUpdate(itemId, scrabCnt);
        String[] favored = item.getFavored().split(",");
        ItemDetailResponseDto itemDetailResponseDto = new ItemDetailResponseDto(
                user.getId(),
                itemId,
                user.getNickname(),
                user.getDegree(),
                user.getGrade(),
                user.getProfile(),
                item.getStatus(),
                itemImgList,
                bagInfos,
                item.getTitle(),
                item.getContents(),
                item.getCreatedAt(),
                item.getViewCnt(),
                scrabCnt,
                item.getType(),
                favored,
                isSrab
        );
        return itemDetailResponseDto;
    }

    // 이승재 / 아이템 구독하기
    @Transactional
    public void scrabItem(Long itemId, UserDetailsImpl userDetails) {

        Long userId = userDetails.getUserId();
        Optional<Scrab> scrab = scrabRepository.findByUserIdAndItemId(userId, itemId);
        if(scrab.isPresent()){
            Long scrabId = scrab.get().getId();
            Scrab scrab1 = scrabRepository.findById(scrabId).orElseThrow(
                    ()-> new IllegalArgumentException("구독 정보가 없습니다.")
            );
            if(scrab1.getScrab().equals(true)) {
                scrab1.update(scrabId, false);
            }else{
                scrab1.update(scrabId, true);
            }
        }else{
            Item item = itemRepository.findById(itemId).orElseThrow(
                    () -> new IllegalArgumentException("아이템 정보가 없습니다.")
            );
            System.out.println(userDetails.getUserId());
            System.out.println(item.getBag().getUserId());
            if (userDetails.getUserId().equals(item.getBag().getUserId())) {
                throw new IllegalArgumentException("본인 아이템입니다.");
            }else {

                Scrab newScrab = Scrab.builder()
                        .userId(userId)
                        .itemId(itemId)
                        .scrab(true)
                        .build();
                scrabRepository.save(newScrab);
            }
        }
        }

    // 이승재 / 아이템 수정 (미리 구현)
    @Transactional
    public void updateItem(ItemRequestDto itemRequestDto, UserDetailsImpl userDetails, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                ()-> new IllegalArgumentException("아이템이 없습니다.")
        );
        List<String> imgUrlList = itemRequestDto.getImgUrl();
        List<String> favoredList = itemRequestDto.getFavored();
        String imgUrl = String.join(",", imgUrlList);
        String favored = String.join(",", favoredList);
        if(item.getBag().getUserId().equals(userDetails.getUserId())){
            item.itemUpdate(itemRequestDto, imgUrl, favored);
        }
    }


    // 이승재 / 아이템 삭제 (미리 구현)
    public void deleteItem(Long itemId, UserDetailsImpl userDetails) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                ()-> new IllegalArgumentException("아이템이 없습니다.")
        );
        if(item.getBag().getUserId().equals(userDetails.getUserId())){
            itemRepository.deleteById(itemId);
        }
    }


    // 이승재 / 유저 스토어 목록 보기
    public UserStoreResponseDto showStore(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new IllegalArgumentException("유저 정보가 없습니다.")
        );
        String nickname = user.getNickname();
        String profile = user.getProfile();
        float grade = user.getGrade();
        String degree = user.getDegree();
        String address = user.getAddress();
        String storeInfo = user.getStoreInfo();

        Long userBadId = bagRepositroy.findByUserId(userId).getId();
        List<Item> myItemList = itemRepository.findAllByBagId(userBadId);
        List<ItemUserResponseDto> itemUserResponseDtos = new ArrayList<>();

        for(Item item : myItemList){
            Long itemId = item.getId();
            String itemImg = item.getItemImg().split(",")[0];
            int status = item.getStatus();
            ItemUserResponseDto itemUserResponseDto = new ItemUserResponseDto(itemId, itemImg, status);
            itemUserResponseDtos.add(itemUserResponseDto);
        }

        return new UserStoreResponseDto(nickname, profile, degree, grade, address, storeInfo, itemUserResponseDtos);

    }

    // 이승재 / 교환신청하기 전 정보
    public TradeInfoDto showTradeInfo(Long itemid, Long userId,  UserDetailsImpl userDetails) {
        Long myBadId = bagRepositroy.findByUserId(userDetails.getUserId()).getId();

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
        Barter barter = barterRepository.save(Barter.builder()
                .buyerId(userDetails.getUserId())
                .sellerId(requestTradeDto.getUserId())
                .barter(StringBarter)
                .status(1)
                .isBuyerScore(false)
                .isBuyerTrade(false)
                .isSellerScore(false)
                .isSellerTrade(false)
                .build());

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

        // 판매자 및 구매자 닉네임
        String buyerNickName = buyer.getNickname();
        String sellerNickName = seller.getNickname();

        // 판매자 아이템 이미지
        Long sellerItemId = Long.valueOf(barter.getBarter().split(";")[1]);
        Item item = itemRepository.findById(sellerItemId).orElseThrow(
                ()-> new IllegalArgumentException("아이템이 없습니다.")
        );
        String sellerItemImage = item.getItemImg().split(",")[0];

        // 구매자 아이템 이미지들
        String buyerItem = barter.getBarter().split(";")[0];
        List<Long> buyerItemId = new ArrayList<>();
        for(int i = 0; i<buyerItem.split(",").length; i++){
            buyerItemId.add(Long.valueOf(buyerItem.split(",")[i]));
        }
        List<String> buyerItemImages = new ArrayList<>();
        for(Long id : buyerItemId){
            Item item1 = itemRepository.findById(id).orElseThrow(
                    ()-> new IllegalArgumentException("아이템이 없습니다.")
            );
            String buyerItemImage = item1.getItemImg().split(",")[0];
            buyerItemImages.add(buyerItemImage);
        }
        return new TradeDecisionDto(buyerNickName, sellerNickName, sellerItemImage, buyerItemImages);
    }


    // 이승재 교환신청 확인 페이지 수락 버튼
    @Transactional
    public void acceptTrade(Long baterId) {
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
    }

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
    }
}

