package com.sparta.mulmul.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity @Getter
@NoArgsConstructor
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

    // 상호 차단과 해제는 추가기능으로 남겨두도록 합니다.
    public static ChatBanned createOf(User user, User bannedUser) {

        ChatBanned banned = new ChatBanned();

        banned.user = user;
        banned.bannedUser = bannedUser;
        banned.isBanned = true;

        return banned;

    }

}
