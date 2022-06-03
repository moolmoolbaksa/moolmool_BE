package com.sparta.mulmul.user.userDto;

import com.sparta.mulmul.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TokenDto {

    private String token;
    private String refresh;
    private Boolean isFirst;

    public static TokenDto createOf(String token, String refresh, User user){

        TokenDto tokenDto = new TokenDto();

        if ( user.getAddress() == null ){ tokenDto.isFirst = true; }
        else { tokenDto.isFirst = false;}
        tokenDto.token = token;
        tokenDto.refresh = refresh;

        return tokenDto;
    }

}
