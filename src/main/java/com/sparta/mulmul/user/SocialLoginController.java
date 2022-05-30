package com.sparta.mulmul.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.mulmul.user.userDto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SocialLoginController {

    private final KakaoUserService kakaoUserService;
    private final GoogleUserService googleUserService;
    private final UserService userService;

    @GetMapping("/user/kakao")
    public ResponseEntity<Map<String, Boolean>> kakaoLogin(@RequestParam String code) throws JsonProcessingException {

        TokenDto tokenDto = kakaoUserService.kakaoLogin(code);

        return userService.sendToken(tokenDto);
    }

    @GetMapping("/user/google")
    public ResponseEntity<Map<String, Boolean>> googleLogin(@RequestParam String code) throws JsonProcessingException {

        TokenDto tokenDto = googleUserService.googleLogin(code);

        return userService.sendToken(tokenDto);
    }
}
