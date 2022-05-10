package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.NotificationDto;
import com.sparta.mulmul.model.Notification;
import com.sparta.mulmul.repository.NotificationRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 알림 전체 목록
    public List<NotificationDto> getNotification(UserDetailsImpl userDetails){

        List<Notification> notifications = notificationRepository.findAllByUserIdOrderByModifiedAtDesc(userDetails.getUserId());
        List<NotificationDto> dtos = new ArrayList<>();

        for (Notification notification : notifications){
            dtos.add(NotificationDto.createFrom(notification));
        }
        return dtos;
    }

    // 알림 삭제
    public void deleteNotification(Long notificationId, UserDetailsImpl userDetails){
        notificationRepository.deleteById(notificationId);
    }

}
