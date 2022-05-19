//package com.sparta.mulmul.model;
//
//import javax.persistence.*;
//
//public class Comment {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private Long userId;
//
//    @Column(nullable = false, unique = true)
//    private String nickname;
//
//    @Column(nullable = false)
//    private String contents;
//
//    @Column(nullable = false)
//    private int status;
//
//    @Column(length = 1000)
//    private String profile;
//
//    @ManyToOne
//    private Comment comment;
//}
