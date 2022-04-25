package com.sparta.mulmul.service;


import com.sparta.mulmul.dto.ItemRequestDto;
import com.sparta.mulmul.model.Item;
import com.sparta.mulmul.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    // 이승재 / 보따리 아이템 등록하기
    public void createItem(ItemRequestDto itemRequestDto){
        List<String> imgUrlList = itemRequestDto.getImgUrl();
        List<String> favoredList = itemRequestDto.getFavored();
        String imgUrl = String.join(",", imgUrlList);
        String favored = String.join(",", favoredList);

        Item item = Item.builder()
                .title(itemRequestDto.getTitle())
                .contents(itemRequestDto.getContents())
                .address("서울")  //유저 정보에서 가져오기
                .category(itemRequestDto.getCategory())
                .scrabCnt(0)
                .commentCnt(0)
                .viewCnt(0)
                .status("교환중")
                .itemImg(imgUrl)
                .type(itemRequestDto.getType())
                .favored(favored)
                .build();

        itemRepository.save(item);

    }
}
