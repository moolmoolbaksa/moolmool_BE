package com.sparta.mulmul.security;

import com.sparta.mulmul.dto.UserRequestDto;
import com.sparta.mulmul.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// 작동구조상, 안의 내용들만 잘 구현해 주면 DB 접촉 없이도 토큰만으로 유효한 자료를 설정해 줄 수 있을 것 같습니다.
public class UserDetailsImpl implements UserDetails {

    private final Long userId;
    private final String nickname;
    private final String profile;

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

    public UserDetailsImpl(UserRequestDto requestDto){
        this.userId = requestDto.getUserId();
        this.nickname = requestDto.getNickname();
        this.profile = requestDto.getProfile();
    }

    public UserDetailsImpl(User user){
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.profile = user.getProfile();
    }

    public UserDetailsImpl(){
        this.userId = null;
        this.nickname = null;
        this.profile = null;
    }

    public static UserDetailsImpl fromUserRequestDto(UserRequestDto requestDto){
        return new UserDetailsImpl(requestDto);
    }

    public static UserDetailsImpl fromUser(User user){
        return new UserDetailsImpl(user);
    }

    public static UserDetailsImpl createEmpty(){
        return new UserDetailsImpl();
    }
}
