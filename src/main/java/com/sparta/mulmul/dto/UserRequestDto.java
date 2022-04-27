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

    public UserRequestDto(Long userId, String nickname){
        this.userId = userId;
        this.nickname = nickname;
    }

    public UserRequestDto(User user){
        this.userId = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.password = user.getPassword();
        this.address = user.getAddress();
        this.profile = user.getProfile();
        this.storeInfo = user.getStoreInfo();
    }

    public static UserRequestDto fromUser(User user){
        return new UserRequestDto(user);
    }

}