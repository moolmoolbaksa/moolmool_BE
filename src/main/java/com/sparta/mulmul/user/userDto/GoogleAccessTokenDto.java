package com.sparta.mulmul.user.userDto;

import lombok.*;

@Data
@Builder
public class GoogleAccessTokenDto {

    private String accessToken;
    private String idToken;

}
