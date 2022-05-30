package com.sparta.mulmul.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.exception.ErrorCode;
import com.sparta.mulmul.exception.ResponseError;
import com.sparta.mulmul.security.jwt.HeaderTokenExtractor;
import com.sparta.mulmul.security.jwt.JwtDecoder;
import com.sparta.mulmul.security.jwt.JwtPreProcessingToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.sparta.mulmul.exception.ErrorCode.*;


/**
 * Token 을 내려주는 Filter 가 아닌  client 에서 받아지는 Token 을 서버 사이드에서 검증하는 클레스 SecurityContextHolder 보관소에 해당
 * Token 값의 인증 상태를 보관 하고 필요할때 마다 인증 확인 후 권한 상태 확인 하는 기능
 */
public class JwtAuthFilter extends AbstractAuthenticationProcessingFilter {

    private final HeaderTokenExtractor extractor;
    private final JwtDecoder jwtDecoder = new JwtDecoder();

    public JwtAuthFilter(
            RequestMatcher requiresAuthenticationRequestMatcher,
            HeaderTokenExtractor extractor
    ) {
        super(requiresAuthenticationRequestMatcher);
        this.extractor = extractor;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException, IOException {

        JwtPreProcessingToken jwtToken;

        // JWT 값을 담아주는 변수 TokenPayload
        String tokenPayload = request.getHeader("Authorization");
        String refreshToken = request.getHeader("RefreshToken");
        String method = request.getMethod();

        if ( refreshToken != null ){

            jwtToken = getJwtPreProcessingToken(request, response, refreshToken);
            if (jwtToken == null) { return null; }

        } else if ( tokenPayload == null && !method.equals("GET") ) {

            setResponseError(response, INVALID_LENGTH_TOKEN);
            return null;

        } else if (tokenPayload == null) {

            jwtToken = new JwtPreProcessingToken("null");

        } else {

            jwtToken = getJwtPreProcessingToken(request, response, tokenPayload);
            if (jwtToken == null) { return null; }

        }

        return super
                .getAuthenticationManager()
                .authenticate(jwtToken);
    }

    private JwtPreProcessingToken getJwtPreProcessingToken(HttpServletRequest request, HttpServletResponse response, String tokenPayload) throws IOException {

        String token;
        JwtPreProcessingToken jwtToken;

        try {
            token = extractor.extract(tokenPayload, request);
        }
        catch (Exception e) {
            setResponseError(response, new CustomException(INVAILD_CONTENTS_TOKEN)
                    .getErrorCode());
            return null;
        }
        try {
            jwtDecoder.expirationCheck(token);
            jwtToken = new JwtPreProcessingToken(token);
        } catch (Exception e) {
            setExpirationError(response, new CustomException(EXPIRATION_TOKEN)
                    .getErrorCode());
            return null;
        }
        return jwtToken;
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException, ServletException {
        /*
         *  SecurityContext 사용자 Token 저장소를 생성합니다.
         *  SecurityContext 에 사용자의 인증된 Token 값을 저장합니다.
         */
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);

        // FilterChain chain 해당 필터가 실행 후 다른 필터도 실행할 수 있도록 연결실켜주는 메서드
        chain.doFilter(
                request,
                response
        );
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException, ServletException {
        /*
         *	로그인을 한 상태에서 Token값을 주고받는 상황에서 잘못된 Token값이라면
         *	인증이 성공하지 못한 단계 이기 때문에 잘못된 Token값을 제거합니다.
         *	모든 인증받은 Context 값이 삭제 됩니다.
         */
        SecurityContextHolder.clearContext();

        super.unsuccessfulAuthentication(
                request,
                response,
                failed
        );
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

    private void setExpirationError(HttpServletResponse response, ErrorCode error) throws IOException {

        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(ResponseError
                .createFrom(error)
        );
        response.getWriter().write(result);
    }
}
