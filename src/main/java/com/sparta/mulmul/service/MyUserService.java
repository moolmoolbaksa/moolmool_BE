package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.*;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserService {


    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BagRepository bagRepository;
    private final ScrabRepository scrabRepository;

    // 성훈_마이페이지_내 정보보기
    public MyPageResponseDto showMyPage(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("user not found")
        );
        Long userId = userDetails.getUserId();
        Long myBagId = bagRepository.findByUserId(userId).getId();

        // 한 유저의 모든 아이템을 보여줌
        List<Item> myItemList = itemRepository.findAllByBagId(myBagId);
        List<ItemUserResponseDto> myItemResponseList = new ArrayList<>();
        // 내 보유 아이템을 리스트 형식으로 담기
        for (Item items : myItemList) {
            ItemUserResponseDto itemResponseDto = new ItemUserResponseDto(
                    items.getId(),
                    items.getItemImg(),
                    items.getStatus()
            );
            myItemResponseList.add(itemResponseDto);
        }

        List<Scrab> scrabList = scrabRepository.findAllByUserId(userId);
        List<ItemUserResponseDto> myScrapItemList = new ArrayList<>();
        for (Scrab eachScrap : scrabList) {
            Long scrapItemId = eachScrap.getItemId();
            // 찜한 아이템
           Item scrapItem = itemRepository.findById(scrapItemId).orElseThrow(
                   () -> new IllegalArgumentException("scrapItem not found")
           );

            ItemUserResponseDto scrabitemDto = new ItemUserResponseDto(
                    scrapItem.getId(),
                    scrapItem.getItemImg(),
                    scrapItem.getStatus()
            );
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

    // 성훈_마이페이지_내 정보수정
    @Transactional
    public UserEditResponseDto editMyPage(String nickname, String address, String storeInfo, List<String> imgUrl, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("user not found")
        );
        String profile = null;
        for (String imgUrls : imgUrl) {
            profile = imgUrls;
        }

        // 유저 정보를 수정
        user.update(
                nickname,
                profile,
                address,
                storeInfo
        );

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
}
