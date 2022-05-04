package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.BarterFinDto;
import com.sparta.mulmul.dto.BarterNotFinDto;
import com.sparta.mulmul.dto.BarterResponseDto;
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
    public BarterResponseDto showMyBarter(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("유저 정보가 없습니다.")
        );

        Long userId = userDetails.getUserId();
        // (거래 물품리스트들과 거래내역의 Id값)이 포함된 거래내역 리스트를 담을 Dto
        BarterResponseDto totalBarter = null;
        // 유저의 거래내역 리스트를 전부 조회한다
        List<Barter> mybarterList = barterRepository.findAllByBuyerIdOrSellerId(userId, userId);
        // 상대방 아이디
        Long opponentId = null;
        // 나의 포지션
        String myPosition = null;

        // 내가 거래한 거래리스트를 대입한다.
        // barterId, buyerId, SellerId를 분리한다.
        for (Barter barters : mybarterList) {
            Long barterId = barters.getId();
            System.out.println("바터아이디 " + barterId);
            LocalDateTime date = barters.getModifiedAt();
            // 거래 물품리스트를 담을 Dto -> 내것과 상대것을 담는다
            List<MyBarterDto> myBarterList = new ArrayList<>();
            List<MyBarterDto> barterList = new ArrayList<>();

            String barter = barters.getBarter();
            //barter 거래내역 id split하기 -> 파싱하여 거래항 물품의 Id값을 찾기
            String[] barterIds = barter.split(";");
            String[] buyerItemIdList = barterIds[0].split(",");
            String[] sellerItemIdList = barterIds[1].split(",");


            // 바이어(유저)의 물품을 찾아서 정보를 넣기
            for (String buyerItemId : buyerItemIdList) {
                Long itemId = Long.parseLong(buyerItemId);
                System.out.println("바이어 아이템 아이디 " + itemId);
                Item buyerItem = itemRepository.findById(itemId).orElseThrow(
                        () -> new IllegalArgumentException("buyerItem not found")
                );

                MyBarterDto buyerItemList = new MyBarterDto(
                        itemId,
                        buyerItem.getTitle(),
                        buyerItem.getItemImg()
                );

                //바이어가 유저라면
                if (buyerItem.getBag().getUserId().equals(userId)) {
                    myBarterList.add(buyerItemList);
                    // 바이어가 유저이기 때문에, 상대방은 셀러가 된다.
                    opponentId = barters.getSellerId();
                    myPosition = "buyer";
                } else {
                    barterList.add(buyerItemList);
                    opponentId = barters.getSellerId();
                    myPosition = "seller";
                }
                System.out.println("바이어아이템 아이디 :" + buyerItem.getId());
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
                        sellerItem.getItemImg()
                );
                //셀러가 유저라면
                if (sellerItem.getBag().getUserId().equals(userId)) {
                    myBarterList.add(sellerItemList);
                } else {
                    barterList.add(sellerItemList);
                }
                System.out.println("셀러아이템 아이디 :" + sellerItem.getItemImg());
            }

            // 상대 유저 정보
            User opponentUser = userRepository.findById(opponentId).orElseThrow(
                    () -> new IllegalArgumentException("유저 정보가 없습니다.")
            );
            System.out.println("유저 네임 :" + opponentUser.getNickname());
            // 거래상태 정보 1 : 신청중 / 2 : 거래중 / 3 : 거래완료 / 4 : 평가완료
            int status = barters.getStatus();
            System.out.println("상태 정보 : " + status);
            System.out.println("거래내역아이디"+barterId);
            System.out.println("상대아이디"+opponentId);
            System.out.println("상대이름"+opponentUser.getNickname());
            System.out.println("상대 이미지"+opponentUser.getProfile());
            System.out.println("상태"+status);
            System.out.println("내포지션"+myPosition);
            System.out.println("내리스트"+myBarterList);
            System.out.println("너리스트"+barterList);


            if (status == 2 || status == 1) {
                BarterNotFinDto barterNotFin = new BarterNotFinDto(
                        barterId,
                        opponentId,
                        opponentUser.getNickname(),
                        opponentUser.getProfile(),
                        status,
                        myPosition,
                        myBarterList,
                        barterList
                );
                BarterFinDto barterFin = new BarterFinDto();
//                BarterResponseDto barterResponseDto = new BarterResponseDto(
//                        barterNotFin,
//                        barterFin
//                );
                assert totalBarter != null;
                totalBarter.addNotFin(barterNotFin);
                System.out.println("상태 2 야호" + status);
                // 거래완료, 평가완료일 경우
            } else if (status == 3 || status == 4) {
                BarterFinDto barterFin = new BarterFinDto(
                        barterId,
                        opponentId,
                        opponentUser.getNickname(),
                        opponentUser.getProfile(),
                        date,
                        status,
                        myPosition,
                        myBarterList,
                        barterList
                );
//                BarterResponseDto barterResponseDto = new BarterResponseDto(
//                        barterNotFin,
//                        barterFin
//                );
                assert totalBarter != null;
                totalBarter.addFin(barterFin);
                System.out.println("상태 4 야호" + status);
            }
        }
        return totalBarter;
    }
}
