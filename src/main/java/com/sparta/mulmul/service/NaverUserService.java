package com.sparta.mulmul.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.mulmul.dto.TokenDto;
import com.sparta.mulmul.dto.user.NaverUserInfoDto;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.model.Bag;
import com.sparta.mulmul.model.Notification;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.BagRepository;
import com.sparta.mulmul.repository.NotificationRepository;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static com.sparta.mulmul.exception.ErrorCode.BANNED_USER;
import static com.sparta.mulmul.security.RestLoginSuccessHandler.TOKEN_TYPE;
import static com.sparta.mulmul.security.jwt.JwtTokenUtils.*;

@Service
@RequiredArgsConstructor
public class NaverUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BagRepository bagRepository;
    private final NotificationRepository notificationRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    private RestTemplate rt = new RestTemplate();

    @Value("${env.naver.secret}")
    private String secret;

    @Value("${env.naver.client}")
    private String id;

    public TokenDto naverLogin(String code, String state) throws JsonProcessingException {

        // 네이버 서버로 액세스스 토큰 요청
        System.out.println("code: " + code + " state: " + state);
        System.out.println("naver: 검증 시작");
        String accessToken = getAccessToken(code, state);
        System.out.println("accessToken: " + accessToken);
        System.out.println("naver: 토큰 검증 완료");
        // 토큰으로 네이버 API 호출
        NaverUserInfoDto naverUserInfo = getNaverUserInfo(accessToken);
        // 회원가입과 로그인 처리 및 유저 정보 받아오기
        User naverUser = registerUserIfNeeded(naverUserInfo);
        // 토큰 Dto 만들기
        return TokenDto.createOf(
                getJwtToken(naverUser, ACCESS_TOKEN),
                getJwtToken(naverUser, REFRESH_TOKEN),
                naverUser
        );
    }

    private String getAccessToken(String code, String state) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", id);
        body.add("client_secret", secret);
        body.add("code", code);
        body.add("state", state);
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest =
                new HttpEntity<>(body, headers);
        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );
        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        return objectMapper.readTree(response.getBody()).get("access_token").asText();
    }

    private NaverUserInfoDto getNaverUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverUserInfoRequest,
                String.class
        );
        return NaverUserInfoDto
                .fromJsonNode(objectMapper.readTree(response.getBody()));
    }

    private User registerUserIfNeeded(NaverUserInfoDto naverUserInfo) {

        User signupUser = userRepository.findByUsername(naverUserInfo.getEmail())
                .orElse(null);
        // 기존에 있는 이메일로 연결되게 구성합니다.
        if ( signupUser == null ){
            // DB 에 중복된 Naver Id 가 있는지 확인
            String naverId = naverUserInfo.getId();
            User naverUser = userRepository.findByNaverId(naverId)
                    .orElse(null);

            if (naverUser == null) {
                String password = passwordEncoder.encode(UUID.randomUUID().toString());

                naverUser = userRepository.save(
                        User.fromNaverUserWithPassword(naverUserInfo, password)
                );
                bagRepository.save(new Bag(naverUser));
                // 회원가입 알림 메시지 저장
                notificationRepository.save(
                        Notification.createFrom(naverUser));
            } else {
                if ( naverUser.getReportCnt() >= 5 ){ // 신고 누적시 처리 진행
                    throw new CustomException(BANNED_USER);
                }
            }
            return naverUser;
        } else {
            if ( signupUser.getReportCnt() >= 5 ){ // 신고 누적시 처리 진행
                throw new CustomException(BANNED_USER);
            }
            return signupUser;
        }

    }
    // JWT 토큰 추출
    private String getJwtToken(User kakaoUser, String tokenType){
        return TOKEN_TYPE + " " + generateJwtToken(
                UserDetailsImpl.fromUser(kakaoUser), tokenType
        );
    }

}
