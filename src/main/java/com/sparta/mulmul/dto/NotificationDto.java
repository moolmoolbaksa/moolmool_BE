package com.sparta.mulmul.dto;

import com.sparta.mulmul.model.Barter;
import com.sparta.mulmul.model.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationDto {

    private Long notificationId;
    private Long changeId;
    private String message;
    private Boolean isRead;
    private NotificationType type;

    public static NotificationDto createFrom(Notification notification){
        NotificationDto dto = new NotificationDto();

        dto.notificationId = notification.getId();
        dto.changeId = notification.getChangeId();
        dto.message = notification.getMessage();
        dto.isRead = notification.getIsRead();
        dto.type = notification.getType();

        return dto;
    }
}
