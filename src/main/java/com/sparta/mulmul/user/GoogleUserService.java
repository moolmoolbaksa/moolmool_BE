package com.sparta.mulmul.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.user.userDto.GoogleAccessTokenDto;
import com.sparta.mulmul.user.userDto.GoogleUserInfoDto;
import com.sparta.mulmul.user.userDto.TokenDto;
import com.sparta.mulmul.websocket.Notification;
import com.sparta.mulmul.websocket.NotificationRepository;
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
import static com.sparta.mulmul.security.jwt.JwtTokenUtils.generateJwtToken;

@Service
@RequiredArgsConstructor
public class GoogleUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BagRepository bagRepository;
    private final NotificationRepository notificationRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    private RestTemplate rt = new RestTemplate();

    @Value("${google.client-id}")
    private String id;

    @Value("${google.secret-key}")
    private String secret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    public TokenDto googleLogin(String code) throws JsonProcessingException {
        // 구글 서버로 요청
        GoogleAccessTokenDto accessToken = getAccessToken(code);
        // 구글 서버로 재차 요청 by access token
        GoogleUserInfoDto googleUserInfo = getGoogleUserInfo(accessToken);
        // 회원가입과 로그인 처리 및 유저 정보 받아오기
        User googleUser = registerUserIfNeeded(googleUserInfo);
        // 토큰 Dto 만들기
        return TokenDto.createOf(
                getJwtToken(googleUser),
                googleUser
        );
    }

    private GoogleAccessTokenDto getAccessToken(String code) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", id);
        body.add("client_secret", secret);
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        ResponseEntity<String> response = rt.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        return GoogleAccessTokenDto.builder()
                .accessToken(objectMapper.readTree(response.getBody()).get("access_token").asText())
                .idToken(objectMapper.readTree(response.getBody()).get("id_token").asText())
                .build();
    }

    private GoogleUserInfoDto getGoogleUserInfo(GoogleAccessTokenDto accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken.getAccessToken());
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> googleUserInfoRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                "https://oauth2.googleapis.com/tokeninfo?id_token=" + accessToken.getIdToken(),
                HttpMethod.GET,
                googleUserInfoRequest,
                String.class
        );
        return GoogleUserInfoDto
                .fromJsonNode(objectMapper.readTree(response.getBody()));
    }

    private User registerUserIfNeeded(GoogleUserInfoDto googleUserInfo) {

        User signupUser = userRepository.findByUsername(googleUserInfo.getEmail())
                .orElse(null);
        if ( signupUser == null ){
            // DB 에 중복된 Kakao Id 가 있는지 확인
            String googleId = googleUserInfo.getId();
            User googleUser = userRepository.findByGoogleId(googleId)
                    .orElse(null);

            if (googleUser == null) {
                String password = passwordEncoder.encode(UUID.randomUUID().toString());

                googleUser = userRepository.save(
                        User.fromGoogleUserWithPassword(googleUserInfo, password)
                );
                bagRepository.save(new Bag(googleUser));
                // 회원가입 알림 메시지 저장
                notificationRepository.save(
                        Notification.createFrom(googleUser));
            } else {
                if ( googleUser.getReportCnt() >= 5 ){ // 신고 누적시 처리 진행
                    throw new CustomException(BANNED_USER);
                }
            }
            return googleUser;
        } else {
            if ( signupUser.getReportCnt() >= 5 ){ // 신고 누적시 처리 진행
                throw new CustomException(BANNED_USER);
            }
            return signupUser;
        }

    }
    // JWT 토큰 추출
    private String getJwtToken(User kakaoUser){
        return TOKEN_TYPE + " " + generateJwtToken(
                UserDetailsImpl.fromUser(kakaoUser)
        );
    }

}
