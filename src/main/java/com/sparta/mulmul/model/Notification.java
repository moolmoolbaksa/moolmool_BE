package com.sparta.mulmul.model;

import com.sparta.mulmul.dto.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter @Entity
@NoArgsConstructor
public class Notification extends CreationDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long changeId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Boolean isRead;

    @Column(nullable = false)
    private NotificationType type;


    public static Notification createFrom(User kakaoUser){

        Notification notification = new Notification();

        notification.userId = kakaoUser.getId();
        notification.changeId = kakaoUser.getId();
        notification.message = "반가워요! " + kakaoUser.getNickname() + "님, 회원 가입을 축하드려요!";
        notification.isRead = false;
        notification.type = NotificationType.ETC;

        return  notification;
    }

    public static Notification createFrom(Barter barter){

        Notification notification = new Notification();

        notification.userId = barter.getSellerId();
        notification.changeId = barter.getId();
        notification.message = "교환 신청이 도착했습니다!";
        notification.isRead = false;
        notification.type = NotificationType.BARTER;

        return  notification;
    }

    public static Notification createOf(ChatRoom chatRoom, User user){

        Notification notification = new Notification();

        notification.userId = user.getId();
        notification.changeId = chatRoom.getId();
        notification.message = user.getNickname() + "님에게 채팅이 왔어요!";
        notification.isRead = false;
        notification.type = NotificationType.CHAT;

        return notification;
    }

    public void setRead(){ this.isRead = true; }

}
