package com.sparta.mulmul.service;
import com.sparta.mulmul.dto.detailPageDto.DetailPageBagDto;
import com.sparta.mulmul.dto.item.ItemDetailResponseDto;
import com.sparta.mulmul.dto.item.ItemRequestDto;
import com.sparta.mulmul.dto.item.ItemResponseDto;
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
            for(Item item : itemList) {
                if (item.getStatus() == 1 || item.getStatus() == 0) {
                    List<Scrab> scrabs = scrabRepository.findAllByItemId(item.getId());
                    int scrabCnt = 0;
                    for (Scrab scrab1 : scrabs) {
                        if (scrab1.getScrab().equals(true)) {
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
            }
            return items;
        }
        List<Item> itemList = itemRepository.findAllByCategory(category);
        List<ItemResponseDto> items = new ArrayList<>();
        Long userId = userDetails.getUserId();
        for(Item item : itemList) {
            if (item.getStatus() == 0 || item.getStatus() == 1) {
                boolean isScrab;
                if (scrabRepository.findByUserIdAndItemId(userId, item.getId()).isPresent()) {
                    isScrab = true;
                } else {
                    isScrab = false;
                }
                List<Scrab> scrabs = scrabRepository.findAllByItemId(item.getId());
                int scrabCnt = 0;
                for (Scrab scrab1 : scrabs) {
                    if (scrab1.getScrab().equals(true)) {
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

    // 이승재 / 아이템 수정
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


    // 이승재 / 아이템 삭제
    public void deleteItem(Long itemId, UserDetailsImpl userDetails) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                ()-> new IllegalArgumentException("아이템이 없습니다.")
        );
        if(item.getBag().getUserId().equals(userDetails.getUserId())){
            itemRepository.deleteById(itemId);
        }
    }
}
