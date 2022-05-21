package com.sparta.mulmul.controller;

import com.sparta.mulmul.dto.*;
import com.sparta.mulmul.dto.user.UserCheckResponseDto;
import com.sparta.mulmul.dto.user.UserRequestDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// 유저 회원가입과 로그인 관련 처리 담당
@RestController
@RequiredArgsConstructor
public class UserController {

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

}