package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.item.ItemStarDto;
import com.sparta.mulmul.model.Barter;
import com.sparta.mulmul.model.Item;
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
        List<Barter> barterList = barterRepository.findAllByBarter(status);
        List<ItemStarDto> itemStarDtoList = new ArrayList<>();

        Map<String, Integer> map = new HashMap<>();
        int cnt = 0;
        for (Barter eachBarter : barterList) {
            String sellerItem = eachBarter.getBarter().split(";")[1];
            Integer count = map.get(sellerItem);
            if (count == null) {
                map.put(sellerItem, 1);
            } else {
                map.put(sellerItem, count + 1);
            }
            cnt++;
            // 1000번째에 정지 - 페이징처리 개선시 없엘 예정
            if (cnt == 1000) {
                break;
            }
        }

        List<String> listKeySet = new ArrayList<>(map.keySet());
        // 내림차순
        Collections.sort(listKeySet, (value1, value2) -> (map.get(value2).compareTo(map.get(value1))));

        cnt = 0;
        for (String key : listKeySet) {
//            System.out.println("key : " + key + " , " + "value : " + map.get(key));
            Long sellerItemId = Long.parseLong(key);
            Item sellerItem = itemRepository.findById(sellerItemId).orElseThrow(
                    () -> new IllegalArgumentException("아이템 정보가 없습니다."));

            ItemStarDto itemStar = new ItemStarDto(
                    sellerItem.getId(),
                    sellerItem.getItemImg().split(",")[0],
                    sellerItem.getTitle(),
                    sellerItem.getContents()
            );
            itemStarDtoList.add(itemStar);
            cnt++;
            if (cnt == 5) {
                break;
            }
        }
        return itemStarDtoList;
    }
}

