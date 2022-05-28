package com.sparta.mulmul.websocket;

import com.sparta.mulmul.websocket.chatDto.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserIdOrderByIdDesc(Long userId);

    int countNotificationByUserIdAndIsReadIsFalse(Long userId);

    void deleteByChangeIdAndType(Long changeId, NotificationType type);

}
