package com.sparta.mulmul.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.mulmul.dto.KakaoUserInfoDto;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.UUID;

import static com.sparta.mulmul.security.RestLoginSuccessHandler.TOKEN_TYPE;

@Service
@RequiredArgsConstructor
public class KakaoUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private HttpHeaders headers = new HttpHeaders();
    private RestTemplate rt = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();

    public String kakaoLogin(String code) throws JsonProcessingException {

        String accessToken = getAccessToken(code);
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
        return getJwtToken(kakaoUserInfo);

    }

    private String getAccessToken(String code) throws JsonProcessingException {

        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "6117b8849303b224556a558baf39dcb9");
        body.add("redirect_uri", "http://localhost:8080/user/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        System.out.println(responseBody);
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);

        rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        rt.setErrorHandler(new DefaultResponseErrorHandler(){
            public boolean hasError(ClientHttpResponse response) throws IOException {
                HttpStatus statusCode = response.getStatusCode();
                return statusCode.series() == HttpStatus.Series.SERVER_ERROR;
            }
        });

        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        System.out.println(responseBody);
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        return KakaoUserInfoDto
                .fromJsonNode(jsonNode);
    }

    private String getJwtToken(KakaoUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);

        if (kakaoUser == null) {
            String password = passwordEncoder.encode(UUID.randomUUID().toString());
            kakaoUser = userRepository.save(
                    User.fromKakaoUserWithPassword(kakaoUserInfo, password)
            );
        }

        return TOKEN_TYPE + " " + JwtTokenUtils.generateJwtToken(
                UserDetailsImpl.fromUser(kakaoUser)
        );
    }

}
