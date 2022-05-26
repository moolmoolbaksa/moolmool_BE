package com.sparta.mulmul.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.mulmul.dto.TokenDto;
import com.sparta.mulmul.service.KakaoUserService;
import com.sparta.mulmul.service.NaverUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.sparta.mulmul.security.RestLoginSuccessHandler.AUTH_HEADER;
import static com.sparta.mulmul.security.RestLoginSuccessHandler.REFRESH_HEADER;

@RestController
@RequiredArgsConstructor
public class SocialLoginController {

    private final KakaoUserService kakaoUserService;
    private final NaverUserService naverUserService;

    @GetMapping("/user/kakao")
    public ResponseEntity<TokenDto> kakaoLogin(@RequestParam String code) throws JsonProcessingException {

        TokenDto tokenDto = kakaoUserService.kakaoLogin(code);
        String token = tokenDto.getAccessToken();
        tokenDto.setAccessToken(null);
        // 토큰 추가 처리 필요

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTH_HEADER, token);
        headers.add(REFRESH_HEADER, token);

        System.out.println(token);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(tokenDto);
    }

    @GetMapping("/user/naver/redirect")
    public ResponseEntity<TokenDto> naverLogin(@RequestParam String code,
                                               @RequestParam String state) throws JsonProcessingException {

        TokenDto tokenDto = naverUserService.naverLogin(code, state);
        String token = tokenDto.getAccessToken();
        tokenDto.setAccessToken(null);
        // 토큰 추가 처리 필요

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTH_HEADER, token);
        headers.add(REFRESH_HEADER, token);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(tokenDto);
    }

}
