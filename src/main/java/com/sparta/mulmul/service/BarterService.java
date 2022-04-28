package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.BarterItemResponseDto;
import com.sparta.mulmul.dto.BarterResponseDto;
import com.sparta.mulmul.model.Barter;
import com.sparta.mulmul.model.Item;
import com.sparta.mulmul.repository.BarterRepository;
import com.sparta.mulmul.repository.ItemRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BarterService {
    private final BarterRepository barterRepository;
    private final ItemRepository itemRepository;

    // 성훈 - 거래내역서 보기
    public List<BarterResponseDto> showMyBarter(UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();

        // USERID? 셀러이거나 바이어 일때는 어떻게 구분하지? -> BuyerIdOrSellerId
        // 유저의 거래내역 리스트를 전부 조회한다
        List<Barter> mybarterList = barterRepository.findAllByBuyerIdOrSellerId(userId, userId);
//        barterRepository.findAllBySellerId(userId);
//        barterRepository.findAllByBuyerId(userId);
        // (거래 물품리스트들과 거래내역의 Id값)이 포함된 거래내역 리스트를 담을 Dto
        List<BarterResponseDto> barterResponseDtoList = new ArrayList<>();
        // 거래 물품리스트를 담을 Dto
        List<BarterItemResponseDto> barterResponseDtosList = null;
        barterResponseDtosList = new ArrayList<>();



        // 내가 거래한 거래리스트를 대입한다.
        // barterId, buyerId, SellerId를 분리한다.
        for (Barter barters : mybarterList) {
            Long barterId = barters.getId();
            System.out.println("바터아이디 " + barterId);
            // 거래내역 1 -> 1,2;3, 4;5,6
            // 거래내역2 -> 1,2;3, 4;5,6
            //barter 거래내역 id split하기 -> 파싱하여 거래항 물품의 Id값을 찾기
            String barter = barters.getBarter();
            System.out.println("거래내역 ID" + barter);
            String[] barterIds = barter.split(";");
            String[] buyerItemIdList = barterIds[0].split(",");
            String[] sellerItemIdList = barterIds[1].split(",");

            System.out.println("바이어 리스트 " + buyerItemIdList);
            System.out.println("셀러 리스트 " + sellerItemIdList);


            // 바이어(유저)의 물품을 찾아서 정보를 넣기
            for (String buyerItemId : buyerItemIdList) {
                Long itemId = Long.parseLong(buyerItemId);
                System.out.println("바이어 아이템 아이디 " + itemId);
                Item buyerItem = itemRepository.getById(itemId);
                BarterItemResponseDto BuyerItemList = new BarterItemResponseDto(
                        itemId,
                        buyerItem.getTitle(),
                        buyerItem.getItemImg(),
                        buyerItem.getCreatedAt(),
                        buyerItem.getStatus());
                barterResponseDtosList.add(BuyerItemList);
            }

            //셀러(유저)의 물품을 찾아서 정보를 넣기
            for (String sellerItemId : sellerItemIdList) {
                Long itemId = Long.parseLong(sellerItemId);
                System.out.println("셀러 아이템 아이디 " + itemId);
                Item sellerItem = itemRepository.getById(itemId);
                BarterItemResponseDto sellerItemList = new BarterItemResponseDto(
                        itemId,
                        sellerItem.getTitle(),
                        sellerItem.getItemImg(),
                        sellerItem.getCreatedAt(),
                        sellerItem.getStatus());
                barterResponseDtosList.add(sellerItemList);
            }

            for(BarterItemResponseDto barterItemResponseDto : barterResponseDtosList){
                System.out.println("아이템 아이디 " + barterItemResponseDto.getItemId());
            }

            // 거래Id와 모든 거래 물품을 넣어준다
            BarterResponseDto barderResponse = new BarterResponseDto(barterId, barterResponseDtosList);
            barterResponseDtoList.add(barderResponse);
        }
        for(BarterResponseDto barterResponseDto : barterResponseDtoList){
            System.out.println("완성본 " + barterResponseDto.getMyItem().toString());
        }
        return barterResponseDtoList;
    }
}
