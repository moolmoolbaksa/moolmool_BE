package com.sparta.mulmul.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.mulmul.dto.TokenDto;
import com.sparta.mulmul.service.KakaoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.sparta.mulmul.security.RestLoginSuccessHandler.AUTH_HEADER;

@RestController
@RequiredArgsConstructor
public class KakaoUserController {

    private final KakaoUserService kakaoUserService;

    @GetMapping("/user/kakao")
    public ResponseEntity<TokenDto> kakaoLogin(@RequestParam String code) throws JsonProcessingException {

        TokenDto tokenDto = kakaoUserService.kakaoLogin(code);
        String token = tokenDto.getToken();
        tokenDto.setToken(null);

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTH_HEADER, token);

        System.out.println(token);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(tokenDto);
    }
}
