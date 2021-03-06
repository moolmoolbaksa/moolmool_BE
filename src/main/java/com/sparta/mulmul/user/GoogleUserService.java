package com.sparta.mulmul.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.security.UserDetailsImpl;

import com.sparta.mulmul.security.jwt.JwtTokenUtils;

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

import static com.sparta.mulmul.security.jwt.JwtTokenUtils.*;

@Service
@RequiredArgsConstructor
public class GoogleUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BagRepository bagRepository;
    private final NotificationRepository notificationRepository;

    private final JwtTokenUtils jwtTokenUtils;


    private ObjectMapper objectMapper = new ObjectMapper();
    private RestTemplate rt = new RestTemplate();

    @Value("${google.client-id}")
    private String id;

    @Value("${google.secret-key}")
    private String secret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    public TokenDto googleLogin(String code) throws JsonProcessingException {
        // ?????? ????????? ??????
        GoogleAccessTokenDto accessToken = getAccessToken(code);
        // ?????? ????????? ?????? ?????? by access token
        GoogleUserInfoDto googleUserInfo = getGoogleUserInfo(accessToken);
        // ??????????????? ????????? ?????? ??? ?????? ?????? ????????????
        User googleUser = registerUserIfNeeded(googleUserInfo);
        // ?????? Dto ?????????
        return TokenDto.createOf(
                jwtTokenUtils.getJwtToken(googleUser, ACCESS_TOKEN),
                jwtTokenUtils.getJwtToken(googleUser, REFRESH_TOKEN),
                googleUser
        );
    }

    private GoogleAccessTokenDto getAccessToken(String code) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP Body ??????
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", id);
        body.add("client_secret", secret);
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        // HTTP ?????? ?????????
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        ResponseEntity<String> response = rt.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
        // HTTP ?????? (JSON) -> ????????? ?????? ??????
        return GoogleAccessTokenDto.builder()
                .accessToken(objectMapper.readTree(response.getBody()).get("access_token").asText())
                .idToken(objectMapper.readTree(response.getBody()).get("id_token").asText())
                .build();
    }

    private GoogleUserInfoDto getGoogleUserInfo(GoogleAccessTokenDto accessToken) throws JsonProcessingException {
        // HTTP Header ??????
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken.getAccessToken());
        // HTTP ?????? ?????????
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
            // DB ??? ????????? Kakao Id ??? ????????? ??????
            String googleId = googleUserInfo.getId();
            User googleUser = userRepository.findByGoogleId(googleId)
                    .orElse(null);

            if (googleUser == null) {
                String password = passwordEncoder.encode(UUID.randomUUID().toString());

                googleUser = userRepository.save(
                        User.fromGoogleUserWithPassword(googleUserInfo, password)
                );
                bagRepository.save(new Bag(googleUser));
                // ???????????? ?????? ????????? ??????
                notificationRepository.save(
                        Notification.createFrom(googleUser));
            } else {
                if ( googleUser.getReportCnt() >= 5 ){ // ?????? ????????? ?????? ??????
                    throw new CustomException(BANNED_USER);
                }
            }
            return googleUser;
        } else {
            if ( signupUser.getReportCnt() >= 5 ){ // ?????? ????????? ?????? ??????
                throw new CustomException(BANNED_USER);
            }
            return signupUser;
        }

    }

}
