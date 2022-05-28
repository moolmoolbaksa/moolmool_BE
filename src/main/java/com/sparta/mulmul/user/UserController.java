package com.sparta.mulmul.user;

import com.sparta.mulmul.dto.*;
import com.sparta.mulmul.item.scrabDto.MyScrabItemDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.image.AwsS3Service;
import com.sparta.mulmul.user.userDto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// 유저 회원가입과 로그인 관련 처리 담당
@RestController
@RequiredArgsConstructor
public class UserController {

    private final MyUserService myUserService;
    private final AwsS3Service awsS3Service;
    private final UserService userService;

    // 주소 설정
    @PutMapping("/user/info")
    public ResponseEntity<OkDto> setUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestBody UserRequestDto requestDto) {
        userService.setUserInfo(userDetails, requestDto);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

    // 회원 정보 체크
    @GetMapping("/user/check")
    public UserCheckResponseDto userCheck(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.userCheck(userDetails);
    }

    /*성훈 - 마이페이지 내 정보 보기*/
    @GetMapping("/user/mypage")
    public MyPageResponseDto showMyPageage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myUserService.showMyPage(userDetails);
    }

    /*성훈 - 마이페이지 내 정보 수정*/
    @PostMapping("/user/mypage")
    public UserEditResponseDto showMyPageage(@RequestParam(value = "nickname", required = false) String nickname,
                                             @RequestParam(value = "profile", required = false) MultipartFile multipartFile,
                                             @RequestParam(value = "address", required = false) String address,
                                             @RequestParam(value = "storeInfo", required = false) String storeInfo,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        System.out.println(multipartFile);
        String imgUrl = awsS3Service.mypageUpdate(multipartFile, userDetails);
        return myUserService.editMyPage(nickname, address, storeInfo, imgUrl, userDetails);

    }

    // 이승재 / 찜한 아이템 보여주기

    @GetMapping("/user/mypage/scrabs")
    public List<MyScrabItemDto> scrabItem(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return myUserService.scrabItem(userDetails);
    }

    // 이승재 / 유저 스토어 목록 보기
    @GetMapping("/user/store/{userId}")
    private UserStoreResponseDto showStore(@PathVariable Long userId){
        return myUserService.showStore(userId);
    }


    // 이승재 / 유저 신고 기능
    @PutMapping("/user/report")
    public ResponseEntity<OkDto> reportUser(@RequestParam Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        String answer =  myUserService.reportUser(userId, userDetails);
        if(answer.equals("true")) {
            return ResponseEntity.ok().body(OkDto.valueOf("true"));
        }else {
            return ResponseEntity.ok().body(OkDto.valueOf("false"));
        }
    }
}