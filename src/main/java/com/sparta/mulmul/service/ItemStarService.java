package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.barter.BarterHotItemListDto;
import com.sparta.mulmul.dto.barter.BarterItemListDto;
import com.sparta.mulmul.dto.barter.HotBarterDto;
import com.sparta.mulmul.dto.item.ItemStarDto;
import com.sparta.mulmul.repository.BarterRepository;
import com.sparta.mulmul.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemStarService {

    private final ItemRepository itemRepository;
    private final BarterRepository barterRepository;


    public List<ItemStarDto> hotItem() {
        int status = 1;
//        List<Barter> barterList = barterRepository.findAllByBarter(status);
        List<HotBarterDto> barterDtoList = barterRepository.findByHotBarter(status);
        List<ItemStarDto> itemStarDtoList = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        searchSellerItem(barterDtoList, map);

        List<String> listKeySet = new ArrayList<>(map.keySet());
        // 내림차순
        Collections.sort(listKeySet, (value1, value2) -> (map.get(value2).compareTo(map.get(value1))));

        int cnt = 0;
        brackTopThree(itemStarDtoList, map, cnt, listKeySet);
        return itemStarDtoList;
    }

    private void searchSellerItem(List<HotBarterDto> barterList, Map<String, Integer> map) {
        for (HotBarterDto eachBarter : barterList) {
            String sellerItem = eachBarter.getBarter().split(";")[1];
            Integer count = map.get(sellerItem);
            if (count == null) {
                map.put(sellerItem, 1);
            } else {
                map.put(sellerItem, count + 1);
            }
        }
    }

    private void brackTopThree(List<ItemStarDto> itemStarDtoList, Map<String, Integer> map, int cnt, List<String> listKeySet) {
        for (String key : listKeySet) {
            System.out.println("key : " + key + " , " + "value : " + map.get(key));
            Long sellerItemId = Long.parseLong(key);
            BarterHotItemListDto sellerItem = itemRepository.findByHotBarterItems(sellerItemId);
            if (sellerItem.getStatus() == 0 || sellerItem.getStatus() == 1) {
                ItemStarDto itemStar = new ItemStarDto(
                        sellerItem.getItemId(),
                        sellerItem.getItemImg().split(",")[0],
                        sellerItem.getTitle(),
                        sellerItem.getContents()
                );
                itemStarDtoList.add(itemStar);
                cnt++;
            }
            if (cnt == 3) {
                break;
            }
        }
    }
}

