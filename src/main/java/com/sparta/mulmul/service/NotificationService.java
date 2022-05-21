package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.NotificationDto;
import com.sparta.mulmul.dto.NotificationType;
import com.sparta.mulmul.model.ChatRoom;
import com.sparta.mulmul.model.Notification;
import com.sparta.mulmul.repository.NotificationRepository;
import com.sparta.mulmul.repository.chat.ChatRoomRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ChatRoomRepository roomRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    // 알림 전체 목록
    public List<NotificationDto> getNotification(UserDetailsImpl userDetails){

        List<Notification> notifications = notificationRepository.findAllByUserIdOrderByIdDesc(userDetails.getUserId());
        List<NotificationDto> dtos = new ArrayList<>();

        for (Notification notification : notifications){
            if ( notification.getType() == NotificationType.CHAT ) {
                ChatRoom chatRoom = roomRepository.findByIdFetch(notification.getChangeId())
                        .orElseThrow( () -> new NullPointerException("NotificationService: 채팅방이 존재하지 않습니다."));
                if ( chatRoom.getAcceptor().getId() == userDetails.getUserId() ) {
                    dtos.add(NotificationDto.createOf(notification, chatRoom.getRequester()));
                } else {
                    dtos.add(NotificationDto.createOf(notification, chatRoom.getAcceptor()));
                }
            } else { dtos.add(NotificationDto.createFrom(notification)); }

        }
        return dtos;
    }

    // 읽음 상태 업데이트
    @Transactional
    public void setRead(Long notificationId){
        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow( () -> new NullPointerException("NotificationService: " + notificationId + "번 알림이 존재하지 않습니다."));

        notification.setRead();
    }
}
