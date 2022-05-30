package com.sparta.mulmul.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.mulmul.security.jwt.JwtTokenUtils;
import com.sparta.mulmul.user.userDto.KakaoUserInfoDto;
import com.sparta.mulmul.user.userDto.TokenDto;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.websocket.Notification;
import com.sparta.mulmul.websocket.NotificationRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static com.sparta.mulmul.exception.ErrorCode.*;
import static com.sparta.mulmul.security.RestLoginSuccessHandler.TOKEN_TYPE;
import static com.sparta.mulmul.security.jwt.JwtTokenUtils.*;

@Service
@RequiredArgsConstructor
public class KakaoUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BagRepository bagRepository;
    private final NotificationRepository notificationRepository;
    private final JwtTokenUtils jwtTokenUtils;

    private ObjectMapper objectMapper = new ObjectMapper();
    private RestTemplate rt = new RestTemplate();

    @Value("${kakao.client-id}")
    private String id;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public TokenDto kakaoLogin(String code) throws JsonProcessingException {
        // 카카오 서버로 요청
        String accessToken = getAccessToken(code);
        // 카카오 서버로 재차 요청 by access token
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
        // 회원가입과 로그인 처리 및 유저 정보 받아오기
        User kakaoUser = registerUserIfNeeded(kakaoUserInfo);
        // 토큰 Dto 만들기
        return TokenDto.createOf(
                jwtTokenUtils.getJwtToken(kakaoUser, ACCESS_TOKEN),
                jwtTokenUtils.getJwtToken(kakaoUser, REFRESH_TOKEN),
                kakaoUser
        );
    }

    private String getAccessToken(String code) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", id);
        // https://moolmooldoctor.firebaseapp.com/auth/kakao/callback
        body.add("redirect_uri", redirectUri);
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
        return objectMapper.readTree(response.getBody()).get("access_token").asText();
    }

    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );
        return KakaoUserInfoDto
                .fromJsonNode(objectMapper.readTree(response.getBody()));
    }

    private User registerUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {

        User signupUser = userRepository.findByUsername(kakaoUserInfo.getEmail())
                .orElse(null);
        if ( signupUser == null ){
            // DB 에 중복된 Kakao Id 가 있는지 확인
            Long kakaoId = kakaoUserInfo.getId();
            User kakaoUser = userRepository.findByKakaoId(kakaoId)
                    .orElse(null);

            if (kakaoUser == null) {
                String password = passwordEncoder.encode(UUID.randomUUID().toString());

                kakaoUser = userRepository.save(
                        User.fromKakaoUserWithPassword(kakaoUserInfo, password)
                );
                bagRepository.save(new Bag(kakaoUser));
                // 회원가입 알림 메시지 저장
                notificationRepository.save(
                        Notification.createFrom(kakaoUser));
            } else {
                if ( kakaoUser.getReportCnt() >= 5 ){ // 신고 누적시 처리 진행
                    throw new CustomException(BANNED_USER);
                }
            }
            return kakaoUser;
        } else {
            if ( signupUser.getReportCnt() >= 5 ){ // 신고 누적시 처리 진행
                throw new CustomException(BANNED_USER);
            }
            return signupUser;
        }
    }
}
