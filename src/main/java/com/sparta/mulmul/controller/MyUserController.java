package com.sparta.mulmul.controller;

import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.dto.user.MyPageResponseDto;
import com.sparta.mulmul.dto.scrab.MyScrabItemDto;
import com.sparta.mulmul.dto.user.UserEditResponseDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.AwsS3Service;
import com.sparta.mulmul.service.MyUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// 유저 회원가입과 로그인 관련 처리 담당
@RestController
@RequiredArgsConstructor
public class MyUserController {

    private final MyUserService myUserService;
    private final AwsS3Service awsS3Service;

    // 아래에서 부터 주어지는 return 값은 논의 후 한 가지 방법으로 바뀔 수 있습니다.

    /*성훈 - 마이페이지 내 정보 보기*/
    @GetMapping("/api/mypage")
    public MyPageResponseDto showMyPageage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myUserService.showMyPage(userDetails);
    }

    /*성훈 - 마이페이지 내 정보 수정*/
    @PostMapping("/api/mypage")
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

    @GetMapping("/api/mypage/scrab")
    public List<MyScrabItemDto> scrabItem(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return myUserService.scrabItem(userDetails);
    }

    // 이승재 / 유저 신고 기능
    @PutMapping("/api/user/report")
    public ResponseEntity<OkDto> reportUser(@RequestParam Long userId){
        myUserService.reportUser(userId);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }
}