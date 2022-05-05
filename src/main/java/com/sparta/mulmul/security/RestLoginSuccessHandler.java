package com.sparta.mulmul.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.security.jwt.JwtTokenUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_TYPE = "BEARER";

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {

        final UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Token 생성
        final String token = JwtTokenUtils.generateJwtToken(userDetails);
        response.addHeader(AUTH_HEADER, TOKEN_TYPE + " " + token);
        response.setContentType("application/json;charset=utf-8");

        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString( OkDto.valueOf("true") );
        response.getWriter().write(result);
    }

}
