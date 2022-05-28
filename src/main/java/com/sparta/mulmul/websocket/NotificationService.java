package com.sparta.mulmul.websocket;

import com.sparta.mulmul.websocket.chatDto.NotificationDto;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.websocket.chat.ChatRoomRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.sparta.mulmul.websocket.chatDto.NotificationType.*;
import static com.sparta.mulmul.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ChatRoomRepository roomRepository;

    // 알림 전체 목록
//    @Cacheable(cacheNames = "notificationInfo", key = "#userDetails.userId")
    public List<NotificationDto> getNotification(UserDetailsImpl userDetails){

        List<Notification> notifications = notificationRepository.findAllByUserIdOrderByIdDesc(userDetails.getUserId());
        List<NotificationDto> dtos = new ArrayList<>();

        for (Notification notification : notifications){
            if ( notification.getType() == CHAT ) {
                ChatRoom chatRoom = roomRepository.findByIdFetch(notification.getChangeId())
                        .orElseThrow( () -> new CustomException(NOT_FOUND_CHAT));
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
                .orElseThrow( () -> new CustomException(NOT_FOUND_NOTIFICATION));

        notification.setRead();
    }
}
