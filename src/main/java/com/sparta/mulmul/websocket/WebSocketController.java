package com.sparta.mulmul.websocket;

import com.sparta.mulmul.websocket.chatDto.MessageRequestDto;
import com.sparta.mulmul.websocket.chatDto.MessageResponseDto;
import com.sparta.mulmul.security.jwt.HeaderTokenExtractor;
import com.sparta.mulmul.security.jwt.JwtDecoder;
import com.sparta.mulmul.websocket.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final ChatMessageService messageService;
    private final NotificationRepository notificationRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    private final HeaderTokenExtractor extractor;
    private final JwtDecoder jwtDecoder;

    // 해당 어노테이션을 통해 웹소켓으로 pub 되는 모든 메시지를 처리하게 됩니다. URI에 자동으로 접두어 /pub 이 붙습니다.
    @MessageMapping("/chat/message")
    public void message(MessageRequestDto requestDto, @Header("Authorization") String token) throws IOException { // 토큰을 헤더에서 받는 것으로 최종 확정함

        String jwt = extractor.extract(token);
        Long userId = jwtDecoder.decodeTokenByUserId(jwt);

        MessageResponseDto responseDto = messageService.saveMessage(requestDto, userId); //DB에 저장
        messageService.sendMessage(requestDto, userId, responseDto);// 메시지를 sub 주소로 발송해줌
    }

    // 커넥트와 디스커넥트 시에는 이 주소로 IN과 OUT의 type으로 요청을 보냅니다.
    @MessageMapping("/chat/connect-status")
    public void connectStatus(MessageRequestDto requestDto) {

        messageService.sendStatus(requestDto); // 동시접속자수 검증
    }

    // 알림 갯수 전달
    @MessageMapping("/notification")
    public void setNotification(@Header("Authorization") String token) throws IOException {

        String jwt = extractor.extract(token);
        Long userId = jwtDecoder.decodeTokenByUserId(jwt);

        Map<String, Integer> map = new HashMap<>();
        map.put("NotificationCnt", notificationRepository.
                        countNotificationByUserIdAndIsReadIsFalse(userId));

        messagingTemplate.convertAndSend("/sub/notification/" + userId, map);
    }
}
