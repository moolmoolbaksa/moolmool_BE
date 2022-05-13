package com.sparta.mulmul.controller;

import com.sparta.mulmul.dto.NotificationCountDto;
import com.sparta.mulmul.dto.chat.MessageRequestDto;
import com.sparta.mulmul.dto.chat.MessageResponseDto;
import com.sparta.mulmul.repository.NotificationRepository;
import com.sparta.mulmul.service.chat.ChatMessageService;
import com.sparta.mulmul.websocket.WsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final ChatMessageService messageService;
    private final NotificationRepository notificationRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    // 해당 어노테이션을 통해 웹소켓으로 pub 되는 모든 메시지를 처리하게 됩니다. URI에 자동으로 접두어 /pub 이 붙습니다.
    @MessageMapping("/chat/message")
    public void message(MessageRequestDto requestDto, WsUser wsUser) {

        MessageResponseDto responseDto = messageService.saveMessage(requestDto, wsUser); //DB에 저장
        messageService.sendMessage(requestDto, wsUser, responseDto);// 메시지를 sub 주소로 발송해줌
    }

    // 커넥트와 디스커넥트 시에는 이 주소로 IN과 OUT의 type으로 요청을 보냅니다.
    @MessageMapping("/chat/connect-status")
    public void connectStatus(MessageRequestDto requestDto, WsUser wsUser, StompSession session) {

        messageService.checkAccess(requestDto, wsUser, session); // 엑세스 권한 검증 -> stomp
        messageService.sendStatus(requestDto); // 동시접속자수 검증
    }

    // 알림 갯수 전달
    @MessageMapping("/notification")
    public void setNotification(WsUser wsUser) {

//        messagingTemplate.convertAndSendToUser(wsUser.getSessionId(),
//                "/sub/notification",
//                NotificationCountDto.valueOf(
//                        notificationRepository.countNotificationByUserIdAndIsReadIsFalse(wsUser.getUserId())
//        ));
        messagingTemplate.convertAndSend("/sub/notification/" + wsUser.getUserId(),
                NotificationCountDto.valueOf(
                        notificationRepository.
                                countNotificationByUserIdAndIsReadIsFalse(wsUser.getUserId())
                )
        );
    }
    // 알림 갯수 전달(테스트)
//    @SubscribeMapping("/notification")
//    public void setTest(WsUser wsUser) {
//
//        messagingTemplate.convertAndSend("/sub/notification/",
//                NotificationCountDto.valueOf(
//                        notificationRepository.
//                                countNotificationByUserIdAndIsReadIsFalse(wsUser.getUserId())
//                )
//        );
//    }
}
