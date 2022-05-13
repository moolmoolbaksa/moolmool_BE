package com.sparta.mulmul.websocket;

import com.sparta.mulmul.dto.WsUserDto;
import lombok.NoArgsConstructor;

import java.security.Principal;

@NoArgsConstructor
public class WsUser implements Principal {

    private String name;
    private WsUserDto userDto;

    public static WsUser createFrom(WsUserDto userDto){
        WsUser user = new WsUser();
        user.userDto = userDto;
        user.name = user.getName();
        return user;
    }

    @Override
    public String getName() { return this.name; }

    public Long getUserId() { return userDto.getUserId();}

    public String getNickname() { return userDto.getNickname(); }

}
