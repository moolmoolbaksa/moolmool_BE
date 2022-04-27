package com.sparta.mulmul.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class User {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String passward;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private int raterCnt;

    @Column(nullable = false)
    private float totalGrade;

    @Column(nullable = false)
    private float grade;

    @Column(nullable = false)
    private String profile;

    @Column(nullable = false)
    private String degree;

    @Column(nullable = false)
    private String storeInfo;

    public void update(String nickname, String profile, String address, String storInfo) {
        this.nickname = nickname;
        this.profile = profile;
        this.address = address;
        this.storeInfo =storInfo;
    }

    public void updateScore(float totalGrade, float grade, int raterCnt, String degree) {
        this.totalGrade = totalGrade;
        this.grade = grade;
        this.raterCnt = raterCnt;
        this.degree = degree;
    }
}
