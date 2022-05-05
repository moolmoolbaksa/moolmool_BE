package com.sparta.mulmul.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.mulmul.service.KakaoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.net.URISyntaxException;

import static com.sparta.mulmul.security.RestLoginSuccessHandler.AUTH_HEADER;

@Controller
@RequiredArgsConstructor
public class KakaoUserControll {

    private final KakaoUserService kakaoUserService;

    @GetMapping("/user/kakao/callback")
    public ResponseEntity<Object> kakaoLogin(@RequestParam String code) throws URISyntaxException, JsonProcessingException {

        String token = kakaoUserService.kakaoLogin(code);

        URI redirectUri = new URI("http://localhost:3000");

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirectUri);
        headers.add(AUTH_HEADER, token);

        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    }


}
