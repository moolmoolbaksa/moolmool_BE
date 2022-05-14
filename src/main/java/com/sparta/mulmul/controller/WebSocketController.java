package com.sparta.mulmul.controller;

import com.sparta.mulmul.dto.chat.MessageRequestDto;
import com.sparta.mulmul.dto.chat.MessageResponseDto;
import com.sparta.mulmul.repository.NotificationRepository;
import com.sparta.mulmul.service.chat.ChatMessageService;
import com.sparta.mulmul.websocket.WsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

        Map<String, Integer> map = new HashMap<>();
        map.put("NotificationCnt", notificationRepository.
                        countNotificationByUserIdAndIsReadIsFalse(wsUser.getUserId()));

        messagingTemplate.convertAndSend("/sub/notification/" + wsUser.getUserId(), map);
    }

    // 교환 요청
    @MessageMapping("/barter")
    public void barter(){


    }
    // 알림 갯수 전달(테스트)
//    @SubscribeMapping("/notification")
//    public void setTest(WsUser wsUser, Principal principal) {
//
//        System.out.println("WebsocketController: @SubscribeMapping에 접근하였습니다.");
//
//        System.out.println("프린시펄 추출: " + principal.getName());
//
//        Map<String, Integer> map = new HashMap<>();
//        map.put("NotificationCnt", notificationRepository.
//                countNotificationByUserIdAndIsReadIsFalse(wsUser.getUserId()));
//
//        messagingTemplate.convertAndSend("/sub/notification", map);
//        System.out.println("WebsocketController: 해쉬맵 제작");
//        messagingTemplate.convertAndSendToUser(principal.getName(), "/sub/notification", map);
//
//        System.out.println("WebsocketController: /sub/notification 으로 전송 완료");
//    }
}
