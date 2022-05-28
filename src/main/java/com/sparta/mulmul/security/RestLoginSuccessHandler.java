package com.sparta.mulmul.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.security.jwt.JwtTokenUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.sparta.mulmul.security.jwt.JwtTokenUtils.*;

public class RestLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public static final String AUTH_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "RefreshToken";
    public static final String TOKEN_TYPE = "BEARER";

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {

        final UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Token 생성
        final String accessToken = generateJwtToken(userDetails, ACCESS_TOKEN);
        final String refreshToken = generateJwtToken(userDetails, REFRESH_TOKEN);
        response.addHeader(AUTH_HEADER, TOKEN_TYPE + " " + accessToken);
        // 로그인 시에 리프레쉬 토큰을 요구하는지 하지 않는지도 검증해야 합니다.
        response.addHeader(REFRESH_HEADER, TOKEN_TYPE + " " + refreshToken);
        response.setContentType("application/json;charset=utf-8");

        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString( OkDto.valueOf("true") );
        response.getWriter().write(result);
    }

}
