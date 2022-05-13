package com.sparta.mulmul.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class WsUserDto {

    private String name;
    private String nickname;
    private Long userId;
    private String sessionId;

}
