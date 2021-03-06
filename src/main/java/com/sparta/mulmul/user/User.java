package com.sparta.mulmul.user;

import com.sparta.mulmul.user.userDto.GoogleUserInfoDto;
import com.sparta.mulmul.user.userDto.KakaoUserInfoDto;
import com.sparta.mulmul.user.userDto.UserRequestDto;
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

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private Long kakaoId;

    @Column(unique = true)
    private String googleId;

    private String address;
    private String profile = "http://kaihuastudio.com/common/img/default_profile.png";
    private String storeInfo;
    private String degree = "물물어린이";
    private int raterCount= 0;
    private float totalGrade;
    private int reportCnt;
    private Boolean isBan;
    private float grade;

    // 회원 정보 초기화 (초기설정을 어떻게 해줄 것인지, 점수 알고리즘이 나오면 다시 만들어 보도록 합니다.)
    public void initProfile(String address){
        this.address = address;
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

    public static User fromGoogleUserWithPassword(GoogleUserInfoDto googleUserInfo, String password){

        User user = new User();
        user.username = googleUserInfo.getEmail();
        user.nickname = googleUserInfo.getNickname();
        user.profile = googleUserInfo.getProfile();
        user.googleId = googleUserInfo.getId();
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

    // 이승재 / 유저 신고하기 기능
    public void reportCntUpdate(Long userId , int reportCnt){
        this.id = userId;
        this.reportCnt = reportCnt;
    }

    // 이승재 / 누적5회신고시 계정 정지
    public void banUser(Long userId, Boolean isBan){
        this.id = userId;
        this.isBan = isBan;
    }
}
