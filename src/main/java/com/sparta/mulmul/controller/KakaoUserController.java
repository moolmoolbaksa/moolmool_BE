package com.sparta.mulmul.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.service.KakaoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

import static com.sparta.mulmul.security.RestLoginSuccessHandler.AUTH_HEADER;

@RestController
@RequiredArgsConstructor
public class KakaoUserController {

    private final KakaoUserService kakaoUserService;

    @GetMapping("/user/kakao")
//    @GetMapping("/user/kakao/callback")
    public ResponseEntity<OkDto> kakaoLogin(@RequestParam String code) throws URISyntaxException, JsonProcessingException {

        String token = kakaoUserService.kakaoLogin(code);

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTH_HEADER, token);

        System.out.println(token);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(OkDto.valueOf("true")
                );
    }
}
