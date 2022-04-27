package com.sparta.mulmul.service;


import com.sparta.mulmul.dto.BagTestDto;
import com.sparta.mulmul.dto.ItemDetailResponseDto;
import com.sparta.mulmul.dto.ItemRequestDto;
import com.sparta.mulmul.dto.ItemResponseDto;
import com.sparta.mulmul.model.Bag;
import com.sparta.mulmul.model.Item;
import com.sparta.mulmul.model.Scrab;
import com.sparta.mulmul.repository.BagRepository;
import com.sparta.mulmul.repository.ItemRepository;
import com.sparta.mulmul.repository.ScrabRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final BagRepository bagRepositroy;
    private final ScrabRepository scrabRepository;

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
                .status("대기중")
                .itemImg(imgUrl)
                .type(itemRequestDto.getType())
                .favored(favored)
                .bag(bag)
                .build();

        itemRepository.save(item);

    }
    //이승재 / 전체 아이템 조회(카테고리별)
    public List<ItemResponseDto> getItems(String category, UserDetailsImpl userDetails) {
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
           ItemResponseDto itemResponseDto = new ItemResponseDto(
                   item.getId(),
                   item.getTitle(),
                   item.getContents(),
                   item.getItemImg().split(",")[0],
                   item.getAddress(),
                   item.getScrabCnt(),
                   item.getViewCnt(),
                   item.getStatus(),
                   isScrab);
           items.add(itemResponseDto);

       }
       return items;
    }


    // 이승재 / 아이템 상세페이지
    public ItemDetailResponseDto getItemDetail(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                ()-> new IllegalArgumentException("아이템이 없습니다.")
        );


        // 아이템에 해당하는 보따리에 담겨있는 모든 아이템 이미지 가져오기
        List<Item> userItemList = itemRepository.findAllByBag(item.getBag().getId());
        List<String> bagImages = new ArrayList<>();
        for(Item item1 : userItemList){
            String repImg =item1.getItemImg().split(",")[0];
            bagImages.add(repImg);
        }
        // 이승재 / 아이템 조회수 계산
        int viewCnt = item.getViewCnt();
        viewCnt += 1;
        item.update(viewCnt);
//        boolean isSarb;
//        if(userRepository.findById(userdetail.getId).isPresent){
//            isSarb = true;
//        }else{
//            isSarb = false;
//        }
        List<String> itemImgList = new ArrayList<>();
        for(int i = 0; i<item.getItemImg().split(",").length; i++){
            itemImgList.add(item.getItemImg().split(",")[i]);
        }

        ItemDetailResponseDto itemDetailResponseDto = new ItemDetailResponseDto(
                //userdetails.getuserId
                (long)1,
                "유저닉네임",
                "유저 등급",
                (float) 4.13,
                "유저 이미지",
                item.getStatus(),
                itemImgList,
                bagImages,
                item.getTitle(),
                item.getContents(),
                item.getCreatedAt(),
                item.getViewCnt(),
                item.getScrabCnt(),
                false
        );
        return itemDetailResponseDto;
    }

    // 이승재 / 아이템 구독하기
    public void scrabItem(Long itemId, UserDetailsImpl userDetails) {

        Long userId = userDetails.getUserId();
        Scrab scrab = scrabRepository.findByUserIdAndItemId(userId, itemId);
        if(){
            Long scrabId = scrabRepository.findByUserIdAndItemId(userId, itemId).getId();
            scrabRepository.deleteById(scrabId);
        }else{
            Scrab scrab = Scrab.builder()
                    .q
        }
    }




    public void createBag(BagTestDto bagTestDto) {
        // 테스트용 보따리 생성
        Bag bag = Bag.builder()
                .itemCnt(0)
                .userId(bagTestDto.getUserId())
                .build();
        bagRepositroy.save(bag);
    }



}
