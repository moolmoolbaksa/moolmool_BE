package com.sparta.mulmul.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.mulmul.exception.ErrorCode;
import com.sparta.mulmul.exception.ResponseError;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.sparta.mulmul.exception.ErrorCode.LOGIN_FAILED;

public class RestFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse httpServletResponse,
                                        AuthenticationException exception) throws IOException, ServletException {

        setResponseError(httpServletResponse, LOGIN_FAILED);
    }

    private void setResponseError(HttpServletResponse response, ErrorCode error) throws IOException {

        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(ResponseError
                .createFrom(error)
        );
        response.getWriter().write(result);
    }
}
