package com.sparta.mulmul.websocket;

import com.sparta.mulmul.websocket.chatDto.NotificationType;

import java.util.List;

public interface NotificationQuerydsl {

    List<Notification> findAllByUserIdAndChangeId(Long userId, Long changeId);

}
