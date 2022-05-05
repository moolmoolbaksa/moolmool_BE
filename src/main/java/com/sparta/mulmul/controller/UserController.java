package com.sparta.mulmul.controller;

import com.sparta.mulmul.dto.*;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.AwsS3Service;
import com.sparta.mulmul.service.UserService;
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

    private final UserService userService;

    private final AwsS3Service awsS3Service;

    // 주소, 프로필 이미지 설정
    @PostMapping("/user/info")
    public ResponseEntity<OkDto> setUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestParam("address") String address,
                                             @RequestParam("profile") List<MultipartFile> multipartFiles,
                                             @RequestParam("storeInfo") String storeInfo) {

        List<String> profile = awsS3Service.uploadFile(multipartFiles);
        userService.setUserInfo(userDetails, new UserRequestDto(address, profile.get(0), storeInfo));
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

    @GetMapping("/user/check")
    public UserCheckResponseDto userCheck(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.userCheck(userDetails);
    }

}