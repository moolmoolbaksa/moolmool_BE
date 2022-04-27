package com.sparta.mulmul.security;

import com.sparta.mulmul.dto.UserRequestDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// 작동구조상, 안의 내용들만 잘 구현해 주면 DB 접촉 없이도 토큰만으로 유효한 자료를 설정해 줄 수 있을 것 같습니다.
public class UserDetailsImpl implements UserDetails {

    private final Long userId;
    private final String nickname;
    private final String profile;

    public UserDetailsImpl(UserRequestDto requestDto){
        this.userId = requestDto.getUserId();
        this.nickname = requestDto.getNickname();
        this.profile = requestDto.getProfile();
    }

    public static UserDetailsImpl fromUserRequestDto(UserRequestDto requestDto){
        return new UserDetailsImpl(requestDto);
    }

    public Long getUserId() { return userId; }
    public String getNickname() { return nickname; }
    public String getProfile() { return profile; }

    @Override
    public String getUsername() { return null; }

    @Override
    public String getPassword() { return null; }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }
}
