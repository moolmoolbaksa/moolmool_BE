package com.sparta.mulmul.service;

import com.amazonaws.services.s3.AmazonS3;
import com.sparta.mulmul.dto.ItemUserResponseDto;
import com.sparta.mulmul.dto.MyPageResponseDto;
import com.sparta.mulmul.dto.UserEditDtailResponseDto;
import com.sparta.mulmul.dto.UserEditResponseDto;
import com.sparta.mulmul.model.Item;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.BagRepository;
import com.sparta.mulmul.repository.ItemRepository;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyUserService {


    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BagRepository bagRepository;

    // 성훈_마이페이지_내 정보보기
    public MyPageResponseDto showMyPage(UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        User user = userRepository.getById(userId);
        Long myBagId = bagRepository.findByUserId(userId).getId();

        // 한 유저의 모든 아이템을 보여줌
        List<Item> myItemList = itemRepository.findAllByBagId(myBagId);
        List<ItemUserResponseDto> myItemResponseDtosList = new ArrayList<>();

        String nickname = user.getNickname();
        String profile = user.getProfile();
        float grade = user.getGrade();
        String degree = user.getDegree();
        String address = user.getAddress();
        String storeInfo = user.getStoreInfo();

        // 내 보유 아이템을 리스트 형식으로 담기
        for (Item items : myItemList) {
            Long itemId = items.getId();
            String itemImg = items.getItemImg();
            int status = items.getStatus();

            ItemUserResponseDto itemResponseDto = new ItemUserResponseDto(itemId, itemImg, status);
            myItemResponseDtosList.add(itemResponseDto);
        }

        // 보내줄 내용을 MyPageResponseDto에 넣어주기
        return new MyPageResponseDto(nickname, profile, degree, grade, address, storeInfo, myItemResponseDtosList);
    }


    // 성훈_마이페이지_내 정보수정
    @Transactional
    public UserEditResponseDto editMyPage(String nickname, String address, String storeInfo, List<String> imgUrl, UserDetailsImpl userDetails) {
        String profile = null;

        // 회원의 정보
        Long userId = userDetails.getUserId();
        User user = userRepository.getById(userId);
        for (String imgUrls : imgUrl){
            profile = imgUrls;
        }

        // 유저 정보를 수정
        user.update(nickname, profile,address,storeInfo);

        // 수정된 정보를 Response하기위해 정보를 넣어 줌
        UserEditDtailResponseDto userEditDtailResponseDto = new UserEditDtailResponseDto(nickname, profile, address, storeInfo);
        // 요청값 반환
        return new UserEditResponseDto(true,userEditDtailResponseDto);
    }
}
