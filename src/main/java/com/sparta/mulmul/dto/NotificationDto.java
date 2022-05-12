package com.sparta.mulmul.dto;

import com.sparta.mulmul.model.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NotificationDto {

    private Long notificationId;
    private Long changeId;
    private String nickname;
    private Boolean isRead;
    private NotificationType type;
    private LocalDateTime date;

    public static NotificationDto createFrom(Notification notification){

        NotificationDto dto = new NotificationDto();

        dto.notificationId = notification.getId();
        dto.changeId = notification.getChangeId();
        dto.nickname = notification.getNickname();
        dto.isRead = notification.getIsRead();
        dto.type = notification.getType();
        dto.date = notification.getCreatedAt();

        return dto;
    }
}
