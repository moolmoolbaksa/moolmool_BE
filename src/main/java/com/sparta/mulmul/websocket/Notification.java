package com.sparta.mulmul.websocket;

import com.sparta.mulmul.barter.Barter;
import com.sparta.mulmul.websocket.chatDto.NotificationType;
import com.sparta.mulmul.utils.CreationDate;
import com.sparta.mulmul.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.sparta.mulmul.websocket.chatDto.NotificationType.*;

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
    private String nickname;

    @Column(nullable = false)
    private Boolean isRead;

    @Column(nullable = false)
    private NotificationType type;


    public static Notification createFrom(User kakaoUser){

        Notification notification = new Notification();

        notification.userId = kakaoUser.getId();
        notification.changeId = kakaoUser.getId();
        notification.nickname = kakaoUser.getNickname();
        notification.isRead = false;
        notification.type = ETC;

        return  notification;
    }

    public static Notification createOf(Barter barter, String nickname){

        Notification notification = new Notification();

        notification.userId = barter.getSellerId();
        notification.changeId = barter.getId();
        notification.nickname = nickname;
        notification.isRead = false;
        notification.type = BARTER;

        return  notification;
    }

    public static Notification createOfBarter(Barter barter, String nickname, String value, String type){

        Notification notification = new Notification();
        if (value.equals( "buyer")){
            notification.userId = barter.getSellerId();
        } else {
            notification.userId = barter.getBuyerId();
        }
        notification.changeId = barter.getId();
        notification.nickname = nickname;
        notification.isRead = false;
        if (type.equals("Barter")){
            notification.type = FINISH;
        } else if (type.equals("Score")) {
            notification.type = SCORE;
        }


        return  notification;
    }

    public static Notification createOf(ChatRoom chatRoom, User user){

        Notification notification = new Notification();

        notification.userId = user.getId();
        notification.changeId = chatRoom.getId();

        if (chatRoom.getRequester().getId().equals(user.getId()) ){
            notification.nickname = chatRoom.getAcceptor().getNickname();
        } else if (chatRoom.getAcceptor().getId().equals(user.getId()) ){
            notification.nickname = chatRoom.getRequester().getNickname();
        }

        notification.isRead = false;
        notification.type = CHAT;

        return notification;
    }

    public void setRead(){ this.isRead = true; }

}