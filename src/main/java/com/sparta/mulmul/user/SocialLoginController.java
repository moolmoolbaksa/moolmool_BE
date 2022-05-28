package com.sparta.mulmul.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.mulmul.user.userDto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.sparta.mulmul.security.RestLoginSuccessHandler.AUTH_HEADER;

@RestController
@RequiredArgsConstructor
public class SocialLoginController {

    private final KakaoUserService kakaoUserService;

    @GetMapping("/user/kakao")
    public ResponseEntity<Map<String, Boolean>> kakaoLogin(@RequestParam String code) throws JsonProcessingException {

        TokenDto tokenDto = kakaoUserService.kakaoLogin(code);
        String token = tokenDto.getToken();

        Map<String, Boolean> map = new HashMap<>();
        map.put("ok", true);
        map.put("isFirst", tokenDto.getIsFirst());

        // 토큰 추가 처리 필요
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTH_HEADER, token);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(map);
    }

}
