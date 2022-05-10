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

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Boolean isRead;

    @Column(nullable = false)
    private NotificationType type;

    public static Notification createOf(String message, User user, NotificationType type){

        Notification notification = new Notification();

        notification.userId = user.getId();
        // 타입에 알맞는 문자를 넣어줘야 합니다. 수정이 필요합니다.
        notification.message = message;
        notification.isRead = false;
        notification.type = type;

        return notification;

    }

}
