package com.sparta.mulmul.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.security.jwt.JwtTokenUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

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
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        // body에 응답값 담아주기::isFirst를 어떻게 세팅할지 생각해 봐야합니다.
        Map<String,Object> res = new HashMap<>();
        res.put("ok", true);
        res.put("isFirst", false);

        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(out, res);
        out.flush();
    }

}
