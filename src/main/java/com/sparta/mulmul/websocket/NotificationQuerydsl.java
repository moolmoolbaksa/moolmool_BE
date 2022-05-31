package com.sparta.mulmul.websocket;

import java.util.List;

public interface NotificationQuerydsl {

    List<Notification> findAllByUserIdAndChangeId(Long userId, Long changeId);

}
