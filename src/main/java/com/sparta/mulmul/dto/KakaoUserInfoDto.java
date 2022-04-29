package com.sparta.mulmul.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

@Getter
public class KakaoUserInfoDto {
    private final Long id;
    private final String email;
    private final String nickname;
    private final String profile;

    public KakaoUserInfoDto(JsonNode jsonNode){
        this.id = jsonNode.get("id").asLong();
        this.nickname = jsonNode.get("properties").get("nickname").asText();
        this.profile = jsonNode.get("properties").get("profile_image").asText();
        this.email = jsonNode.get("kakao_account").get("email").asText();
    }
    public static KakaoUserInfoDto fromJsonNode(JsonNode jsonNode){
        return new KakaoUserInfoDto(jsonNode);
    }
}
