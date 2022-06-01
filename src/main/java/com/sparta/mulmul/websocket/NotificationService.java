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
    public List<NotificationDto> getNotification(UserDetailsImpl userDetails){

        List<Notification> notifications = notificationRepository.findAllByUserIdOrderByIdDesc(userDetails.getUserId());
        List<NotificationDto> dtos = new ArrayList<>();

        for ( Notification noti : notifications ){
            System.out.println(noti.getType() + ": " + noti.getChangeId());
        }

        for (Notification notification : notifications){
            if ( notification.getType() == CHAT ) {
                ChatRoom chatRoom = roomRepository.findByIdFetch(notification.getChangeId())
                        .orElse( null );
                if ( chatRoom != null ) {
                    if ( chatRoom.getAcceptor().getId().equals(userDetails.getUserId()) ) {
                        dtos.add(NotificationDto.createOf(notification, chatRoom.getRequester()));
                    } else if ( chatRoom.getRequester().getId().equals(userDetails.getUserId()) ) {
                        dtos.add(NotificationDto.createOf(notification, chatRoom.getAcceptor()));
                    }
                }
            } else {
                dtos.add(NotificationDto.createFrom(notification));
            }
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
