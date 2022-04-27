package com.sparta.mulmul.service;

import com.amazonaws.services.s3.AmazonS3;
import com.sparta.mulmul.dto.ItemResponseDto;
import com.sparta.mulmul.dto.MyPageResponseDto;
import com.sparta.mulmul.dto.UserEditDtailResponseDto;
import com.sparta.mulmul.dto.UserEditResponseDto;
import com.sparta.mulmul.model.Item;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.ImageRepository;
import com.sparta.mulmul.repository.ItemRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    private final ImageRepository imageRepository;
    private final ItemRepository itemRepository;

    // 성훈_마이페이지_내 정보보기
    public MyPageResponseDto showMyPage(UserDetailsImpl userDetails) {
        User user = userDetails.getUser;
        Long userId = user.getId();

        // 한 유저의 모든 아이템을 보여줌
        List<Item> myItemList = itemRepository.findAllByUserId(userId);
        List<ItemResponseDto> itemResponseDtosList = new ArrayList<>();

        String nickname = user.getNickname();
        String profile = "프로필.jpg";
        float grade = user.getGrade();
        String degree = "물물박사";
        String address = user.getAddress();
        String storeInfo = user.getStoreInfo();

        // 내 보유 아이템을 리스트 형식으로 담기
        for (Item items : myItemList) {
            Long itemId = items.getId();
            String itemImg = items.getItemImg();
            ItemResponseDto itemResponseDto = new ItemResponseDto(itemId, itemImg);
            itemResponseDtosList.add(itemResponseDto);
        }

        // 보내줄 내용을 MyPageResponseDto에 넣어주기
        MyPageResponseDto myPageResponseDto = new MyPageResponseDto(nickname, profile, degree, address, storeInfo, itemResponseDtosList);
        return myPageResponseDto;
    }

    // 성훈_마이페이지_내 정보수정
    // update로하면 수정이되나? 기억이 가물가물하다.
    public UserEditResponseDto editMyPage(String nickname, String address, String storeInfo, List<String> imgUrl, UserDetailsImpl userDetails) {
        UserEditResponseDto userEditResponseDto = null;
        String profile = null;

        // 회원의 정보
        User user = userDetails.getUser;
        for (String imgUrls : imgUrl){
            profile = imgUrls;
        }

        // 유저 정보를 수정
        user.update(nickname, profile,address,storeInfo);

        // 수정된 정보를 Response하기위해 정보를 넣어 줌
        UserEditDtailResponseDto userEditDtailResponseDto = new UserEditDtailResponseDto(nickname, profile, address, storeInfo);
        // 요청값 반환
        userEditResponseDto = new UserEditResponseDto(true,userEditDtailResponseDto);
        return userEditResponseDto;
    }


}
