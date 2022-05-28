package com.sparta.mulmul.user.userDto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverUserInfoDto {
    private String id;
    private String email;
    private String nickname;
    private String profile;

    // JsonNode 로부터 NaverUserInfoDto 가져오기
    public static NaverUserInfoDto fromJsonNode(JsonNode jsonNode){

        NaverUserInfoDto naverUserInfoDto = new NaverUserInfoDto();

        naverUserInfoDto.id = jsonNode.get("response").get("id").asText();
        naverUserInfoDto.nickname = jsonNode.get("response").get("nickname").asText();
        try{
            naverUserInfoDto.profile = jsonNode.get("response").get("profile_image").asText();
        }
        catch (Exception e){
            naverUserInfoDto.profile = "http://kaihuastudio.com/common/img/default_profile.png";
        }

        naverUserInfoDto.email = jsonNode.get("response").get("email").asText();

        return naverUserInfoDto;
    }
}
