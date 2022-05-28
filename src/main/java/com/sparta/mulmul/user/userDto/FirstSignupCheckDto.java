package com.sparta.mulmul.user.userDto;

import lombok.Getter;

@Getter
public class FirstSignupCheckDto {
    private final Boolean ok = true;
    private final Boolean isFirst;

    public FirstSignupCheckDto( String profile ){
        if( profile == null ) { this.isFirst = true; }
        else{ this.isFirst = false; }
    }

    public static FirstSignupCheckDto fromProfile( String profile ){
        return new FirstSignupCheckDto(profile);
    }
}
