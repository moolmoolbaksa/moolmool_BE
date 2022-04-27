package com.sparta.mulmul.service;


import com.sparta.mulmul.dto.BagTestDto;
import com.sparta.mulmul.dto.ItemDetailResponseDto;
import com.sparta.mulmul.dto.ItemRequestDto;
import com.sparta.mulmul.dto.ItemResponseDto;
import com.sparta.mulmul.model.Bag;
import com.sparta.mulmul.model.Item;
import com.sparta.mulmul.model.Scrab;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.BagRepository;
import com.sparta.mulmul.repository.ItemRepository;
import com.sparta.mulmul.repository.ScrabRepository;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public ItemDetailResponseDto getItemDetail(Long itemId, UserDetailsImpl userDetails) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                ()-> new IllegalArgumentException("아이템이 없습니다.")
        );


        // 아이템에 해당하는 보따리에 담겨있는 모든 아이템 이미지 가져오기
        List<Item> userItemList = itemRepository.findAllByBagId(item.getBag().getId());
        List<String> bagImages = new ArrayList<>();
        for(Item item1 : userItemList){
            String repImg =item1.getItemImg().split(",")[0];
            bagImages.add(repImg);
        }
        // 이승재 / 아이템 조회수 계산
        int viewCnt = item.getViewCnt();
        viewCnt += 1;
        item.update(viewCnt);

        // 이승재 / 아이템 구독 정보 유저 정보를 통해 확인
        boolean isSarb;
        if(scrabRepository.findByUserIdAndItemId(userDetails.getUserId(), itemId).isPresent()){
            isSarb = true;
        }else{
            isSarb = false;
        }

        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                ()-> new IllegalArgumentException("유저 정보가 없습니다.")
        );

        List<String> itemImgList = new ArrayList<>();
        for(int i = 0; i<item.getItemImg().split(",").length; i++){
            itemImgList.add(item.getItemImg().split(",")[i]);
        }

        ItemDetailResponseDto itemDetailResponseDto = new ItemDetailResponseDto(
                //userdetails.getuserId
                (long)1,
                user.getNickname(),
                user.getGrade(),
                user.getProfile(),
                item.getStatus(),
                itemImgList,
                bagImages,
                item.getTitle(),
                item.getContents(),
                item.getCreatedAt(),
                item.getViewCnt(),
                item.getScrabCnt(),
                isSarb
        );
        return itemDetailResponseDto;
    }

    // 이승재 / 아이템 구독하기
    public void scrabItem(Long itemId, UserDetailsImpl userDetails) {

        Long userId = userDetails.getUserId();
        Optional<Scrab> scrab = scrabRepository.findByUserIdAndItemId(userId, itemId);
        if(scrab.isPresent()){
            Long scrabId = scrabRepository.findByUserIdAndItemId(userId, itemId).get().getId();
            scrabRepository.deleteById(scrabId);
        }else{
            Scrab newScrab = Scrab.builder()
                    .userId(userId)
                    .itemId(itemId)
                    .build();
            scrabRepository.save(newScrab);
        }
    }



}