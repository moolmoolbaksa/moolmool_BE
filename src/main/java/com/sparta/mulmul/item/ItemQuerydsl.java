package com.sparta.mulmul.item;

import com.sparta.mulmul.barter.barterDto.BarterHotItemListDto;
import com.sparta.mulmul.barter.barterDto.BarterItemListDto;
import com.sparta.mulmul.item.itemDto.ItemUserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemQuerydsl {

     Page<Item> findAllItemOrderByCreatedAtDesc(Pageable pageable);

     Page<Item> findAllItemByCategoryOrderByCreatedAtDesc(String category, Pageable pageable);

     List<Item> searchByKeyword (String keyword);

     List<ItemUserResponseDto> findByMyPageItems(Long userId);

     List<ItemUserResponseDto> findByMyScrabItems(Long userId);

     BarterItemListDto findByBarterItems(Long itemId);

     BarterHotItemListDto findByHotBarterItems(Long itemId);

}
