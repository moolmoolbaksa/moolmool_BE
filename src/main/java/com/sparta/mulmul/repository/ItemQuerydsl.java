package com.sparta.mulmul.repository;

import com.sparta.mulmul.dto.barter.BarterHotItemListDto;
import com.sparta.mulmul.dto.barter.BarterItemListDto;
import com.sparta.mulmul.dto.barter.HotBarterDto;
import com.sparta.mulmul.dto.item.ItemUserResponseDto;
import com.sparta.mulmul.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemQuerydsl {

     Page<Item> findAllItemOrderByCreatedAtDesc(Pageable pageable);

     Page<Item> findAllItemByCategoryOrderByCreatedAtDesc(String category, Pageable pageable);

     List<ItemUserResponseDto> findByMyPageItems(Long userId);

     List<ItemUserResponseDto> findByMyScrabItems(Long userId);

     BarterItemListDto findByBarterItems(Long itemId);

     BarterHotItemListDto findByHotBarterItems(Long itemId);

}
