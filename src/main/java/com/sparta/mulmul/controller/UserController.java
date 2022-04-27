package com.sparta.mulmul.controller;

import com.sparta.mulmul.dto.MyPageResponseDto;
import com.sparta.mulmul.dto.UserEditResponseDto;
import com.sparta.mulmul.service.AwsS3Service;
import com.sparta.mulmul.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RequiredArgsConstructor
@RestController
public class UserController {

    private  final UserService userService;
    private final AwsS3Service awsS3Service;

    /*성훈 - 마이페이지 내 정보 보기*/
    @GetMapping("/api/mypage")
    public MyPageResponseDto showMyPageage (@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.showMyPage(userDetails);
    }

    /*성훈 - 마이페이지 내 정보 수정*/
    @PutMapping("/api/mypage")
    public UserEditResponseDto showMyPageage (@RequestParam("nickname") String nickname,
                                              @RequestParam("profile") List<MultipartFile> multipartFile,
                                              @RequestParam("address") String address,
                                              @RequestParam("storeInfo") String storeInfo,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        List<String> imgUrl = awsS3Service.uploadFile(multipartFile, userDetails);
        return userService.editMyPage(nickname, address, storeInfo, imgUrl, userDetails);
    }

}
