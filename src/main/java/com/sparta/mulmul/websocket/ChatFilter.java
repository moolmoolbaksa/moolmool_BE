package com.sparta.mulmul.websocket;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class ChatFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filter;
}
