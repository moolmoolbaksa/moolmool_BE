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

    // 아래에서 부터 주어지는 return 값은 논의 후 한 가지 방법으로 바뀔 수 있습니다.

    // 회원가입
    @PostMapping("/user/signup")
    public ResponseEntity<OkDto> signup(@RequestBody UserRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.ok().body(OkDto.of("true"));
    }

    // 아이디 중복 체크
    @PostMapping("/user/id-check")
    public ResponseEntity<OkDto> idCheck(@RequestBody UserRequestDto requestDto) {
        userService.checkBy("username", requestDto);
        return ResponseEntity.ok().body(OkDto.of("true"));
    }

    // 닉네임 중복 체크
    @PostMapping("/user/nickname-check")
    public ResponseEntity<OkDto> nickCheck(@RequestBody UserRequestDto requestDto) {
        userService.checkBy("nickname", requestDto);
        return ResponseEntity.ok().body(OkDto.of("true"));
    }

    // 주소, 프로필 이미지 설정
    @PostMapping("/user/info")
    public ResponseEntity<OkDto> setUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestBody UserRequestDto requestDto) {
        userService.setUserInfo(userDetails, requestDto);
        return ResponseEntity.ok().body(OkDto.of("true"));
    }

    @GetMapping("/user/check")
    public UserCheckResponseDto userCheck(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.userCheck(userDetails);
    }

    /*성훈 - 마이페이지 내 정보 보기*/
    @GetMapping("/api/mypage")
    public MyPageResponseDto showMyPageage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.showMyPage(userDetails);
    }

    /*성훈 - 마이페이지 내 정보 수정*/
    @PutMapping("/api/mypage")
    public UserEditResponseDto showMyPageage(@RequestParam("nickname") String nickname,
                                             @RequestParam("profile") List<MultipartFile> multipartFile,
                                             @RequestParam("address") String address,
                                             @RequestParam("storeInfo") String storeInfo,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<String> imgUrl = awsS3Service.uploadFile(multipartFile, userDetails);
        return userService.editMyPage(nickname, address, storeInfo, imgUrl, userDetails);

    }
}