package com.sparta.mulmul.model;

import com.sparta.mulmul.dto.UserRequestDto;
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

    private String address;
    private String profile;
    private String storeInfo;
    private String degree;
    private int raterCount;
    private float totalGrade;
    private float grade;

    // 회원가입 생성자
    public User(UserRequestDto requestDto, String password){
        this.username = requestDto.getUsername();
        this.nickname = requestDto.getNickname();
        this.password = password;
    }
    // 회원 정보 초기화 (초기설정을 어떻게 해줄 것인지, 점수 알고리즘이 나오면 다시 만들어 보도록 합니다.)
    public void initUserInfo(UserRequestDto requestDto){
        this.address = requestDto.getAddress();
        if ( requestDto.getProfile() == null ) { this.profile = "setDefaultURL"; }
        else { this.profile = requestDto.getProfile(); }
        this.storeInfo = requestDto.getStoreInfo();
    }

    // 정적 팩토리 메소드
    public static User withPassword(UserRequestDto requestDto, String password){
        return new User(requestDto, password);
    }

    public void update(String nickname, String profile, String address, String storInfo) {
        this.nickname = nickname;
        this.profile = profile;
        this.address = address;
        this.storeInfo =storInfo;
    }

    public void updateScore(float totalGrade, float grade, int raterCount, String degree) {
        this.totalGrade = totalGrade;
        this.grade = grade;
        this.raterCount = raterCount;
        this.degree = degree;
    }

}
