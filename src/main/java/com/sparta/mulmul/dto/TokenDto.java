package com.sparta.mulmul.dto;

import com.sparta.mulmul.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TokenDto {

    private String accessToken;
    private String refreshToken;
    private Boolean isFirst;

    public static TokenDto createOf(String accessToken, String refreshToken,User user){

        TokenDto tokenDto = new TokenDto();

        if ( user.getAddress() == null ){ tokenDto.isFirst = true; }
        else { tokenDto.isFirst = false;}
        tokenDto.accessToken = accessToken;
        tokenDto.refreshToken = refreshToken;

        return tokenDto;
    }

}
