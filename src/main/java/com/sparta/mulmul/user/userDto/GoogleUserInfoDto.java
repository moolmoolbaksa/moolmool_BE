package com.sparta.mulmul.user.userDto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserInfoDto {

    private String id;
    private String email;
    private String nickname;
    private String profile;

    // JsonNode 로부터 KakaoUserInfoDto 가져오기
    public static GoogleUserInfoDto fromJsonNode(JsonNode jsonNode){

        GoogleUserInfoDto googleUserInfoDto = new GoogleUserInfoDto();

        googleUserInfoDto.id = jsonNode.get("sub").asText();
        googleUserInfoDto.nickname = jsonNode.get("name").asText();
        try{
            googleUserInfoDto.profile = jsonNode.get("picture").asText();
        }
        catch (Exception e){
            googleUserInfoDto.profile = "http://kaihuastudio.com/common/img/default_profile.png";
        }

        googleUserInfoDto.email = jsonNode.get("email").asText();

        return googleUserInfoDto;
    }

}
