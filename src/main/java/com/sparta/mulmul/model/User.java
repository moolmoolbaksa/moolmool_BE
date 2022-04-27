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
    // 회원 정보 초기화
    public void initProfile(UserRequestDto requestDto){
        this.address = requestDto.getAddress();
        this.profile = requestDto.getProfile();
        this.storeInfo = requestDto.getStoreInfo();
        this.raterCount = 0;
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
