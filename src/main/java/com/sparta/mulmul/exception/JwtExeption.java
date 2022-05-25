package com.sparta.mulmul.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class JwtExeption {

    private final int httpStatus;
    private final String code;
    private final String message;

    public void setError(HttpServletResponse response, String errorCode, String message) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        Map<String, Object> map = new HashMap<>();
        map.put("ok", false);
        map.put("code", errorCode);
        map.put("message", message);
        String result = mapper.writeValueAsString(map);
        response.getWriter().print(result);
    }

}
