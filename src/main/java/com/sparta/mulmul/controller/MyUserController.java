package com.sparta.mulmul.controller;

import com.sparta.mulmul.dto.MyPageResponseDto;
import com.sparta.mulmul.dto.MyScrabItemDto;
import com.sparta.mulmul.dto.UserEditResponseDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.AwsS3Service;
import com.sparta.mulmul.service.MyUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    @PutMapping("/api/mypage")
    public UserEditResponseDto showMyPageage(@RequestParam("nickname") String nickname,
                                             @RequestParam("profile") List<MultipartFile> multipartFile,
                                             @RequestParam("address") String address,
                                             @RequestParam("storeInfo") String storeInfo,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<String> imgUrl = awsS3Service.uploadFile(multipartFile, userDetails);
        return myUserService.editMyPage(nickname, address, storeInfo, imgUrl, userDetails);

    }

    // 이승재 / 찜한 아이템 보여주기

    @GetMapping("/api/mypage/scrab")
    public List<MyScrabItemDto> scrabItem(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return myUserService.scrabItem(userDetails);
    }
}