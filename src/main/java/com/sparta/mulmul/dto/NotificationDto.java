package com.sparta.mulmul.dto;

import com.sparta.mulmul.dto.item.ItemStarDto;
import com.sparta.mulmul.model.Notification;
import com.sparta.mulmul.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class NotificationDto {

    private Long notificationId;
    private Long changeId;
    private String nickname;
    private Boolean isRead;
    private NotificationType type;
    private LocalDateTime date;
    private List<ItemStarDto> itemList;

    private Long userId;
    private String profile;

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

    public static NotificationDto createFrom(Notification notification, List<ItemStarDto> itemList){

        NotificationDto dto = new NotificationDto();

        dto.notificationId = notification.getId();
        dto.changeId = notification.getChangeId();
        dto.nickname = notification.getNickname();
        dto.isRead = notification.getIsRead();
        dto.type = notification.getType();
        dto.date = notification.getCreatedAt();
        dto.itemList = itemList;

        return dto;
    }

    public static NotificationDto createOf(Notification notification, User user){

        NotificationDto dto = new NotificationDto();

        dto.notificationId = notification.getId();
        dto.changeId = notification.getChangeId();
        dto.nickname = notification.getNickname();
        dto.isRead = notification.getIsRead();
        dto.type = notification.getType();
        dto.date = notification.getCreatedAt();

        dto.userId = user.getId();
        dto.profile = user.getProfile();

        return dto;
    }

}
