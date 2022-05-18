package com.sparta.mulmul.model;

import lombok.Getter;

import javax.persistence.*;

@Entity @Getter
public class ChatBanned {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User bannedUser;

    @Column(nullable = false)
    private Boolean isBanned;

    public void releaseBanned(){ this.isBanned = false; }

}
