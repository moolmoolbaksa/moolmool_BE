package com.sparta.mulmul.security.provider;

import com.sparta.mulmul.dto.UserRequestDto;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class RestLoginAuthProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;
    public RestLoginAuthProvider(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        // FormLoginFilter 에서 생성된 토큰으로부터 아이디와 비밀번호를 조회함
        String username = token.getName();
        String password = (String) token.getCredentials();

        // UserDetailsService 를 통해 DB에서 username 으로 사용자 조회
        UserRequestDto requestDto = UserRequestDto
                .fromUser(userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다.")));

        UserDetailsImpl userDetails = UserDetailsImpl.fromUserRequestDto(requestDto);

        if (!passwordEncoder.matches(password, requestDto.getPassword())) {
            throw new BadCredentialsException(requestDto.getUsername() + "Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
