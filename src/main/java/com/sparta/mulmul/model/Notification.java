package com.sparta.mulmul.model;

import com.sparta.mulmul.dto.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter @Entity
@NoArgsConstructor
public class Notification extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String message;
    private Boolean isRead;
    private NotificationType type;

    public static Notification createFrom(ChatMessage message, User user, NotificationType type){

        Notification notification = new Notification();

        notification.userId = user.getId();
        // 타입에 알맞는 문자를 넣어줘야 합니다. 수정이 필요합니다.
        notification.message = message.getMessage();
        notification.isRead = false;
        notification.type = type;

        return notification;

    }

}
