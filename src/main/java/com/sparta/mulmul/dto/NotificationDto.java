package com.sparta.mulmul.dto;

import com.sparta.mulmul.model.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationDto {

    private Long id;
    private String message;
    private Boolean isRead;
    private NotificationType type;

    public static NotificationDto createFrom(Notification notification){
        NotificationDto dto = new NotificationDto();

        dto.id = notification.getId();
        dto.message = notification.getMessage();
        dto.isRead = notification.getIsRead();
        dto.type = notification.getType();

        return dto;
    }
}
