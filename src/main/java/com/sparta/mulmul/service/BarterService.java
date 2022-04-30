package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.BarterResponseDto;
import com.sparta.mulmul.dto.CompleteBarterDto;
import com.sparta.mulmul.dto.MyBarterDto;
import com.sparta.mulmul.model.Barter;
import com.sparta.mulmul.model.Item;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.BarterRepository;
import com.sparta.mulmul.repository.ItemRepository;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BarterService {
    private final BarterRepository barterRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    // 성훈 - 거래내역서 보기
    public List<BarterResponseDto> showMyBarter(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("유저 정보가 없습니다.")
        );

        Long userId = userDetails.getUserId();
        // (거래 물품리스트들과 거래내역의 Id값)이 포함된 거래내역 리스트를 담을 Dto
        List<BarterResponseDto> barterResponseDtoList = new ArrayList<>();

        // 유저의 거래내역 리스트를 전부 조회한다
        List<Barter> mybarterList = barterRepository.findAllByBuyerIdOrSellerId(userId, userId);

        // 내가 거래한 거래리스트를 대입한다.
        // barterId, buyerId, SellerId를 분리한다.
        for (Barter barters : mybarterList) {
            Long barterId = barters.getId();
            System.out.println("바터아이디 " + barterId);
            LocalDateTime date = barters.getModifiedAt();

            // 거래 물품리스트를 담을 Dto
            List<MyBarterDto> myBarterList = new ArrayList<>();
            List<MyBarterDto> barterList = new ArrayList<>();

            // 거래내역의 상태가 0 -> 즉 거래중일 경우만
            int status = barters.getStatus();
            if (status == 0) {
                String barter = barters.getBarter();
                //barter 거래내역 id split하기 -> 파싱하여 거래항 물품의 Id값을 찾기
                String[] barterIds = barter.split(";");
                String[] buyerItemIdList = barterIds[0].split(",");
                String[] sellerItemIdList = barterIds[1].split(",");


                // 바이어(유저)의 물품을 찾아서 정보를 넣기
                for (String buyerItemId : buyerItemIdList) {
                    Long itemId = Long.parseLong(buyerItemId);
                    System.out.println("바이어 아이디 " + itemId);
                    Item buyerItem = itemRepository.findById(itemId).orElseThrow(
                            () -> new IllegalArgumentException("buyerItem not found")
                    );

                    MyBarterDto buyerItemList = new MyBarterDto(
                            itemId,
                            buyerItem.getTitle(),
                            buyerItem.getItemImg(),
                            buyerItem.getCreatedAt(),
                            buyerItem.getStatus()
                    );
                    if (buyerItem.getBag().getUserId().equals(userId)) {
                        myBarterList.add(buyerItemList);
                    } else {
                        barterList.add(buyerItemList);
                    }
                }

                //셀러(유저)의 물품을 찾아서 정보를 넣기
                for (String sellerItemId : sellerItemIdList) {
                    Long itemId = Long.parseLong(sellerItemId);
                    Item sellerItem = itemRepository.findById(itemId).orElseThrow(
                            () -> new IllegalArgumentException("sellerItem not found")
                    );

                    MyBarterDto sellerItemList = new MyBarterDto(
                            itemId,
                            sellerItem.getTitle(),
                            sellerItem.getItemImg(),
                            sellerItem.getCreatedAt(),
                            sellerItem.getStatus());
                    if (sellerItem.getBag().getUserId().equals(userId)) {
                        myBarterList.add(sellerItemList);
                    } else {
                        barterList.add(sellerItemList);
                    }
                }

                // 거래Id와 모든 거래 물품을 넣어준다
                BarterResponseDto barterResponse = new BarterResponseDto(
                        barterId,
                        date,
                        myBarterList,
                        barterList
                );
                barterResponseDtoList.add(barterResponse);
            }
        }
        return barterResponseDtoList;
    }


    // 성훈 - 거래완료된 내역서보기
    public List<CompleteBarterDto> showMyCompleteBarter(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("유저 정보가 없습니다.")
        );

        Long userId = userDetails.getUserId();
        // (거래 물품리스트들과 거래내역의 Id값)이 포함된 거래내역 리스트를 담을 Dto
        List<CompleteBarterDto> barterResponseDtoList = new ArrayList<>();

        // 유저의 거래내역 리스트를 전부 조회한다
        List<Barter> mybarterList = barterRepository.findAllByBuyerIdOrSellerId(userId, userId);

        // 내가 거래한 거래리스트를 대입한다.
        // barterId, buyerId, SellerId를 분리한다.
        for (Barter barters : mybarterList) {
            Long barterId = barters.getId();
            System.out.println("바터아이디 " + barterId);
            LocalDateTime date = barters.getModifiedAt();

            // 거래 물품리스트를 담을 Dto
            List<MyBarterDto> myBarterList = new ArrayList<>();
            List<MyBarterDto> barterList = new ArrayList<>();

            //거래내역의 상태가 1 -> 즉 거래완료일 경우만
            int status = barters.getStatus();
            if (status >= 1) {
                String barter = barters.getBarter();
                //barter 거래내역 id split하기 -> 파싱하여 거래항 물품의 Id값을 찾기
                String[] barterIds = barter.split(";");
                String[] buyerItemIdList = barterIds[0].split(",");
                String[] sellerItemIdList = barterIds[1].split(",");


                // 바이어(유저)의 물품을 찾아서 정보를 넣기
                for (String buyerItemId : buyerItemIdList) {
                    Long itemId = Long.parseLong(buyerItemId);
                    Item buyerItem = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("item not found"));
                    ;
                    MyBarterDto buyerItemList = new MyBarterDto(
                            itemId,
                            buyerItem.getTitle(),
                            buyerItem.getItemImg(),
                            buyerItem.getCreatedAt(),
                            buyerItem.getStatus()
                    );
                    if (buyerItem.getBag().getUserId().equals(userId)) {
                        myBarterList.add(buyerItemList);
                    } else {
                        barterList.add(buyerItemList);
                    }
                }

                //셀러(유저)의 물품을 찾아서 정보를 넣기
                for (String sellerItemId : sellerItemIdList) {
                    Long itemId = Long.parseLong(sellerItemId);
                    System.out.println("완료 바이어 아이디 " + itemId);
                    Item sellerItem = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("item not found"));
                    ;
                    MyBarterDto sellerItemList = new MyBarterDto(
                            itemId,
                            sellerItem.getTitle(),
                            sellerItem.getItemImg(),
                            sellerItem.getCreatedAt(),
                            sellerItem.getStatus());
                    if (sellerItem.getBag().getUserId().equals(userId)) {
                        myBarterList.add(sellerItemList);
                    } else {
                        barterList.add(sellerItemList);
                    }
                }

                // 거래내역의 Status가 2이면 거래완료이므로 true값을 보낸다.
                boolean isGraded = false;
                if (barters.getStatus() == 2) {
                    isGraded = true;
                    // 거래Id와 모든 거래 물품을 넣어준다
                    CompleteBarterDto completeBarterDto = new CompleteBarterDto(
                            barterId,
                            isGraded,
                            date,
                            myBarterList,
                            barterList
                    );
                    barterResponseDtoList.add(completeBarterDto);
                } else {
                    // Status가 1이므로 false값을 보낸다.
                    CompleteBarterDto completeBarterDto = new CompleteBarterDto(
                            barterId,
                            isGraded,
                            date,
                            myBarterList,
                            barterList
                    );
                    barterResponseDtoList.add(completeBarterDto);
                }
            }
        }
        return barterResponseDtoList;
    }
}
