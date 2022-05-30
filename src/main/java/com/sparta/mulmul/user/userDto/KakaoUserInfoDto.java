package com.sparta.mulmul.user.userDto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {

    private Long id;
    private String email;
    private String nickname;
    private String profile;

    // JsonNode 로부터 KakaoUserInfoDto 가져오기
    public static KakaoUserInfoDto fromJsonNode(JsonNode jsonNode){

        KakaoUserInfoDto kakaoUserInfoDto = new KakaoUserInfoDto();

        kakaoUserInfoDto.id = jsonNode.get("id").asLong();
        kakaoUserInfoDto.nickname = jsonNode.get("properties").get("nickname").asText();
        try{
            kakaoUserInfoDto.profile = jsonNode.get("properties").get("profile_image").asText();
        }
        catch (Exception e){
            kakaoUserInfoDto.profile = "http://kaihuastudio.com/common/img/default_profile.png";
        }

        kakaoUserInfoDto.email = jsonNode.get("kakao_account").get("email").asText();

        return kakaoUserInfoDto;
    }
}
