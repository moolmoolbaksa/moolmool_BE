package com.sparta.mulmul.websocket;

import com.sparta.mulmul.dto.UserRequestDto;
import lombok.NoArgsConstructor;

import java.security.Principal;

@NoArgsConstructor
public class WsUser implements Principal {

    private UserRequestDto requestDto;

    public static WsUser fromUserRequestDto(UserRequestDto requestDto){
        WsUser user = new WsUser();
        user.requestDto = requestDto;
        return user;
    }

    @Override
    public String getName() { return requestDto.getNickname(); }

    public Long getUserId() { return requestDto.getUserId();}

    public String getNickname() { return requestDto.getNickname(); }

}
