//package com.sparta.mulmul.model;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@Entity
//@Getter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class Post extends Timestamped {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private Long userId;
//
//    @Column(nullable = false)
//    private String nickname;
//
//    @Column(nullable = false)
//    private String title;
//
//    @Column(nullable = false)
//    private String contents;
//
//    @Column(nullable = false)
//    private int likeCnt;
//
//    @Column(nullable = false)
//    private int commentCnt;
//
//    @Column(nullable = false)
//    private int viewCnt;
//
//    @Column(nullable = false)
//    private int reportCnt;
//
//    @Column(nullable = false)
//    private int status;
//
//    @Column(length = 1000)
//    private String itemImg;
//
//
//
//}
