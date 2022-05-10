package com.sparta.mulmul.dto;

import com.sparta.mulmul.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TokenDto {

    private Boolean ok;
    private String token;
    private Boolean isFirst;

    public static TokenDto createOf(String token, User user){

        TokenDto tokenDto = new TokenDto();

        tokenDto.ok = true;
        if ( user.getAddress() == null ){ tokenDto.isFirst = true; }
        else { tokenDto.isFirst = false;}
        tokenDto.token = token;

        return tokenDto;
    }

}
