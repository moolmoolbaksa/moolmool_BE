package com.sparta.mulmul.model;

import com.sparta.mulmul.dto.user.KakaoUserInfoDto;
import com.sparta.mulmul.dto.user.UserRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private Long kakaoId;

    private String address;
    private String profile = "http://kaihuastudio.com/common/img/default_profile.png";
    private String storeInfo;
    private String degree = "물물어린이";
    private int raterCount;
    private float totalGrade;
    private float grade;
    private double latitude;
    private double longitude;

    // 회원 정보 초기화 (초기설정을 어떻게 해줄 것인지, 점수 알고리즘이 나오면 다시 만들어 보도록 합니다.)
    public void initProfile(String address){
        this.address = address;
    }

    public static User withPassword(UserRequestDto requestDto, String password){

        User user = new User();
        user.username = requestDto.getUsername();
        user.nickname = requestDto.getNickname();
        user.password = password;

        return user;
    }


    public static User fromKakaoUserWithPassword(KakaoUserInfoDto kakaoUserInfo, String password){

        User user = new User();
        user.username = kakaoUserInfo.getEmail();
        user.nickname = kakaoUserInfo.getNickname();
        user.profile = kakaoUserInfo.getProfile();
        user.kakaoId = kakaoUserInfo.getId();
        user.password = password;

        return user;
    }
    public void update(String nickname, String profile, String address, String storInfo) {

        this.nickname = nickname;
        this.profile = profile;
        this.address = address;
        this.storeInfo =storInfo;

    }

    public void updateFirstScore(float totalGrade, float grade, int raterCount, String degree) {
        this.totalGrade = totalGrade;
        this.grade = grade;
        this.raterCount = raterCount;
        this.degree = degree;
    }

    public void updateSecondScore(float totalGrade, String degree) {
        this.totalGrade = totalGrade;
        this.degree = degree;
    }


    public void execptImageUpdate(String nickname, String address, String storeInfo){
        this.nickname = nickname;
        this.address = address;
        this.storeInfo = storeInfo;
    }

}
