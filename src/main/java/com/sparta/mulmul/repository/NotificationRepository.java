package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserIdOrderByIdDesc(Long userId);

    int countNotificationByUserIdAndIsReadIsFalse(Long userId);

}
