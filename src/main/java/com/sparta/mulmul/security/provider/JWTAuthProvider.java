package com.sparta.mulmul.security.provider;

import com.sparta.mulmul.dto.UserRequestDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.security.jwt.JwtDecoder;
import com.sparta.mulmul.security.jwt.JwtPreProcessingToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JWTAuthProvider implements AuthenticationProvider {

    private final JwtDecoder jwtDecoder;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String token = (String) authentication.getPrincipal();

        Long userId = jwtDecoder.decodeTokenByUserId(token);
        String nickname = jwtDecoder.decodeTokenByNickname(token);

        UserDetailsImpl userDetails = UserDetailsImpl.fromUserRequestDto(new UserRequestDto(userId, nickname));

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtPreProcessingToken.class.isAssignableFrom(authentication);
    }
}
