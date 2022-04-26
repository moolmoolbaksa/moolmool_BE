package com.sparta.mulmul.security.filter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.mulmul.dto.UserRequestDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class RestLoginFilterFilter extends UsernamePasswordAuthenticationFilter {
    final private ObjectMapper objectMapper;

    public RestLoginFilterFilter(final AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
        objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        UsernamePasswordAuthenticationToken authRequest;

        try{
            UserRequestDto requestDto = objectMapper.readValue(request.getReader().lines().collect(Collectors.joining()), UserRequestDto.class);
            String username = requestDto.getUsername();
            String password = requestDto.getPassword();
            authRequest = new UsernamePasswordAuthenticationToken(username, password);
        } catch (IOException e){
            e.printStackTrace();
            throw new AuthenticationServiceException("username, password Null or Fail to Json parsing");
        }

        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
