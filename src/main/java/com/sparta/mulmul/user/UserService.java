package com.sparta.mulmul.user;

import com.sparta.mulmul.security.jwt.HeaderTokenExtractor;
import com.sparta.mulmul.security.jwt.JwtDecoder;
import com.sparta.mulmul.user.userDto.TokenDto;
import com.sparta.mulmul.user.userDto.UserCheckResponseDto;
import com.sparta.mulmul.user.userDto.UserRequestDto;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.sparta.mulmul.exception.ErrorCode.*;
import static com.sparta.mulmul.security.RestLoginSuccessHandler.AUTH_HEADER;
import static com.sparta.mulmul.security.RestLoginSuccessHandler.REFRESH_HEADER;
import static com.sparta.mulmul.security.jwt.JwtTokenUtils.generateAccessToken;

// 유저 서비스
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtDecoder jwtDecoder;
    private final HeaderTokenExtractor extractor;

    // 회원 정보 초기화 시켜주기
    @Transactional
    public void setUserInfo(UserDetailsImpl userDetails, UserRequestDto requestDto) {

        User user = userRepository.findById(userDetails
                        .getUserId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        user.initProfile(requestDto.getAddress());
    }

    // 로그인 체크하기
    public UserCheckResponseDto userCheck(UserDetailsImpl userDetails){

        if ( userDetails.getUserId() == null ) { throw new CustomException(NOT_FOUND_USER); }
        return new UserCheckResponseDto(userRepository
                .findById(userDetails.getUserId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER))
        );
    }

    public String getRefreshToken(String payload) throws IOException {

        Long userId;
        String nickname;

        try {
            String token = extractor.extract(payload);
            userId = jwtDecoder.decodeTokenByUserId(token);
            nickname = jwtDecoder.decodeTokenByNickname(token);
            jwtDecoder.expirationCheck(token);
        } catch (CustomException e) {
            throw new CustomException(INVAILD_CONTENTS_TOKEN);
        }
        return generateAccessToken(UserDetailsImpl
                .fromUserRequestDto(
                        UserRequestDto.createOf(userId, nickname)
                )
        );
    }

    public ResponseEntity<Map<String, Boolean>> sendToken(TokenDto tokenDto){

        Map<String, Boolean> map = new HashMap<>();
        map.put("ok", true);
        map.put("isFirst", tokenDto.getIsFirst());

        // 토큰 추가 처리 필요
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTH_HEADER, tokenDto.getToken());
        headers.add(REFRESH_HEADER, tokenDto.getRefresh());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(map);
    }

}