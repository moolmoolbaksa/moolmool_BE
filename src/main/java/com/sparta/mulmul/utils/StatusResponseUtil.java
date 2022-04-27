package com.sparta.mulmul.utils;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Component
public class StatusResponseUtil {
    public HttpServletResponse setBadRequest(HttpServletResponse response){
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return response;
    }
}
