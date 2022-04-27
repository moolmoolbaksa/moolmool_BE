package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.*;
import com.sparta.mulmul.model.Bag;
import com.sparta.mulmul.model.Item;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.BagRepository;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import com.sparta.mulmul.repository.ItemRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

// 유저 서비스
@Service
@RequiredArgsConstructor
public class UserService {

//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//
//    private final AmazonS3 amazonS3;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BagRepository bagRepository;

    // 회원가입 처리
    public void signup(UserRequestDto requestDto){

        // 회원가입 유효성 검사 실시 (혹시 valid check를 시행할 하나의 공통 메소드를 만들 방법을 연구해 보도록 합니다.)
        checkBy("usernameAndNickname", requestDto);
        // 비밀번호 암호화
        String EncodedPassword = passwordEncoder.encode(requestDto.getPassword());
        // 회원가입 및 반환
        bagRepository.save(new Bag(userRepository.save(
                User.withPassword(requestDto, EncodedPassword)
        ))
        );
    }

    // 아이디 중복 체크
    public void checkBy(String userInfo, UserRequestDto requestDto){

        if (userInfo.equals("username")) { // username에 대한 중복 체크 시행
            if ( userRepository
                    .findByUsername(requestDto.getUsername())
                    .isPresent() )
            { throw new IllegalArgumentException("이메일이 중복됩니다.");}
        } else if (userInfo.equals("nickname")) { // nickname에 대한 중복 체크 시행
            if ( userRepository
                    .findByNickname(requestDto.getNickname())
                    .isPresent() )
            { throw new IllegalArgumentException("닉네임이 중복됩니다.");}
        } else if (userInfo.equals("usernameAndNickname")) {
            if ( userRepository
                    .findByUsername(requestDto.getUsername())
                    .isPresent() )
            { throw new IllegalArgumentException("이메일이 중복됩니다.");}
            if ( userRepository
                    .findByNickname(requestDto.getNickname())
                    .isPresent() )
            { throw new IllegalArgumentException("닉네임이 중복됩니다.");}
        }
        else { // 메소드 인자 입력 오류
            throw new IllegalArgumentException("\"username\", \"nickname\", \"usernameAndNickname\"을 인자로 중복체크를 시행해 주세요.");
        }
    }

    // 회원 정보 초기화 시켜주기
    @Transactional
    public void setUserInfo(UserDetailsImpl userDetails, UserRequestDto requestDto) {

        User user = userRepository.findById(userDetails
                .getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User's not found error"));

        user.initProfile(requestDto);
    }

    // 로그인 체크하기
   public UserCheckResponseDto userCheck(UserDetailsImpl userDetails){

        return new UserCheckResponseDto(userRepository
                .findById(userDetails.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User's not found error"))
        );
    }

    // 성훈_마이페이지_내 정보보기
    public MyPageResponseDto showMyPage(UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        User user = userRepository.getById(userId);
        Long myBagId = bagRepository.findByUserId(userId).getId();

        // 한 유저의 모든 아이템을 보여줌
        List<Item> myItemList = itemRepository.findAllByBagId(myBagId);
        List<ItemUserResponseDto> myItemResponseDtosList = new ArrayList<>();

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
            ItemUserResponseDto itemResponseDto = new ItemUserResponseDto(itemId, itemImg);
            myItemResponseDtosList.add(itemResponseDto);
        }

        // 보내줄 내용을 MyPageResponseDto에 넣어주기
        MyPageResponseDto myPageResponseDto = new MyPageResponseDto(nickname, profile, degree, grade, address, storeInfo, myItemResponseDtosList);
        return myPageResponseDto;
    }


    // 성훈_마이페이지_내 정보수정
    // update로하면 수정이되나? 기억이 가물가물하다.
    @Transactional
    public UserEditResponseDto editMyPage(String nickname, String address, String storeInfo, List<String> imgUrl, UserDetailsImpl userDetails) {
        UserEditResponseDto userEditResponseDto = null;
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
        userEditResponseDto = new UserEditResponseDto(true,userEditDtailResponseDto);
        return userEditResponseDto;
    }

}