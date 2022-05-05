package com.sparta.mulmul.security;

import com.sparta.mulmul.dto.UserRequestDto;
import com.sparta.mulmul.model.User;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// 작동구조상, 안의 내용들만 잘 구현해 주면 DB 접촉 없이도 토큰만으로 유효한 자료를 설정해 줄 수 있을 것 같습니다.
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long userId;
    private String nickname;

    public Long getUserId() { return userId; }
    public String getNickname() { return nickname; }

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

    // UserRequestDto로부터 UserDetailsImpl 생성
    public static UserDetailsImpl fromUserRequestDto(UserRequestDto requestDto){

        UserDetailsImpl userDetails = new UserDetailsImpl();

        userDetails.userId = requestDto.getUserId();
        userDetails.nickname = requestDto.getNickname();

        return userDetails;
    }

    // User로부터 UserDetailsImpl 생성
    public static UserDetailsImpl fromUser(User user){

        UserDetailsImpl userDetails = new UserDetailsImpl();

        userDetails.userId = user.getId();
        userDetails.nickname = user.getNickname();

        return userDetails;
    }

    // 비어있는 UserDetailsImpl 생성
    public static UserDetailsImpl createEmpty(){

        UserDetailsImpl userDetails = new UserDetailsImpl();

        userDetails.userId = null;
        userDetails.nickname = null;

        return userDetails;
    }
}
