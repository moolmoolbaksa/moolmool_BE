package com.sparta.mulmul.service;


import com.sparta.mulmul.dto.item.ItemUserResponseDto;
import com.sparta.mulmul.dto.scrab.MyScrabItemDto;
import com.sparta.mulmul.dto.user.MyPageResponseDto;
import com.sparta.mulmul.dto.user.UserEditDtailResponseDto;
import com.sparta.mulmul.dto.user.UserEditResponseDto;
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

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyUserService {


    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ScrabRepository scrabRepository;
    private final BagRepository bagRepository;

    // 성훈_마이페이지_내 정보보기
    public MyPageResponseDto showMyPage(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("user not found")
        );
        Long userId = userDetails.getUserId();
//        Bag bag = bagRepository.findByUserId(userId);
//        Long bagId = bag.getId();

        // 한 유저의 모든 아이템을 보여줌
        List<Item> myItemList = itemRepository.findAllMyItem(userId);
//        List<Item> myItemList = itemRepository.findAllByBagIdOrBagId(bagId, bagId);
        List<ItemUserResponseDto> myItemResponseList = new ArrayList<>();
        // 내 보유 아이템을 리스트 형식으로 담기
        for (Item items : myItemList) {
            ItemUserResponseDto itemResponseDto = getItemUserDto(items);
            myItemResponseList.add(itemResponseDto);
        }
        List<Scrab> myScrabList = scrabRepository.findTop3ByUserIdAndScrab(userId, true);
        List<ItemUserResponseDto> myScrapItemList = new ArrayList<>();

        for (Scrab myscrap : myScrabList) {

            Long myScrapItemId = myscrap.getItemId();
            Item scrabItem = itemRepository.findById(myScrapItemId).orElseThrow(
                    () -> new IllegalArgumentException("Item not found"));
            ItemUserResponseDto scrabitemDto = getItemUserDto(scrabItem);
            myScrapItemList.add(scrabitemDto);

        }

        // 보내줄 내용을 MyPageResponseDto에 넣어주기
        return new MyPageResponseDto(
                user.getNickname(),
                user.getProfile(),
                user.getDegree(),
                user.getGrade(),
                user.getAddress(),
                user.getStoreInfo(),
                myItemResponseList,
                myScrapItemList
        );
    }

    private ItemUserResponseDto getItemUserDto(Item scrabItem) {
        ItemUserResponseDto scrabitemDto = new ItemUserResponseDto(
                scrabItem.getId(),
                scrabItem.getItemImg().split(",")[0],
                scrabItem.getStatus()
        );
        return scrabitemDto;
    }

    // 성훈_마이페이지_내 정보수정
    @Transactional
    public UserEditResponseDto editMyPage(String nickname, String address, String
            storeInfo, String imgUrl, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("user not found")
        );
        String profile = imgUrl;

        if (imgUrl.equals("empty")){
            user.execptImageUpdate(nickname, address, storeInfo);
        }else {
            // 유저 정보를 수정
            user.update(
                    nickname,
                    profile,
                    address,
                    storeInfo
            );
        }

        // 수정된 정보를 Response하기위해 정보를 넣어 줌
        UserEditDtailResponseDto userEditDtailResponseDto = new UserEditDtailResponseDto(
                nickname,
                profile,
                address,
                storeInfo
        );

        // 요청값 반환
        return new UserEditResponseDto(true, userEditDtailResponseDto);
    }

    // 이승재 / 찜한 아이템 보여주기
    public List<MyScrabItemDto> scrabItem(UserDetailsImpl userDetails) {
        List<Scrab> scrabList = scrabRepository.findAllByUserId(userDetails.getUserId());

        List<MyScrabItemDto> myScrabItemDtoList = new ArrayList<>();
        for (Scrab scrab : scrabList) {
            if (scrab.getScrab().equals(true)) {
                Item item = itemRepository.findById(scrab.getItemId()).orElseThrow(
                        () -> new IllegalArgumentException("아이템 정보가 없습니다.")
                );
                Long itemId = item.getId();
                String title = item.getTitle();
                String contents = item.getContents();
                String image = item.getItemImg().split(",")[0];
                MyScrabItemDto myScrabItemDto = new MyScrabItemDto(itemId, title, contents, image);
                myScrabItemDtoList.add(myScrabItemDto);
            }
        }
        return myScrabItemDtoList;

    }
}