package com.sparta.mulmul.dto;

import com.sparta.mulmul.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDto {
    private Long userId;
    private String username;
    private String nickname;
    private String password;
    private String passwordCheck;
    private String address;
    private String profile;
    private String storeInfo;

    public UserRequestDto(String address, String profile, String storeInfo){
        this.address = address;
        this.profile = profile;
        this.storeInfo = storeInfo;
    }

    // 유저로부터 정보 가져오기
    public static UserRequestDto fromUser(User user){

        UserRequestDto requestDto = new UserRequestDto();

        requestDto.userId = user.getId();
        requestDto.username = user.getUsername();
        requestDto.nickname = user.getNickname();
        requestDto.password = user.getPassword();
        requestDto.address = user.getAddress();
        requestDto.profile = user.getProfile();
        requestDto.storeInfo = user.getStoreInfo();

        return requestDto;
    }

    // userId와 nickname 으로부터 토큰을 생성하기 위해 적용되는 정적 팩토리 메소드
    public static UserRequestDto createOf(Long userId, String nickname){

        UserRequestDto requestDto = new UserRequestDto();

        requestDto.userId = userId;
        requestDto.nickname = nickname;

        return requestDto;
    }



}