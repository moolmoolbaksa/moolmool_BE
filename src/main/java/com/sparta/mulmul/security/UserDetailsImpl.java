package com.sparta.mulmul.service;

import com.sparta.mulmul.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// 작동구조상, 안의 내용들만 잘 구현해 주면 DB 접촉 없이도 토큰만으로 유효한 자료를 설정해 줄 수 있을 것 같습니다.
public class UserDetailsImpl implements UserDetails {

    //////////////////////////////////// 받아온 객체를 바탕으로 뭔가 설정해서 비교하겠다는 뜻임. UserDetailsService에서 User를 가져와서 UserDetails를 만들어 주게 됩니다.
    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
    ////////////////////////////////////

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

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
