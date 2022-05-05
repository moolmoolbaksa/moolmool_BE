package com.sparta.mulmul.controller.websocketController;

import com.sparta.mulmul.dto.chat.MessageRequestDto;
import com.sparta.mulmul.dto.chat.MessageResponseDto;
import com.sparta.mulmul.service.chat.ChatMessageService;
import com.sparta.mulmul.websocket.WsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;

    // 해당 어노테이션을 통해 웹소켓으로 pub 되는 모든 메시지를 처리하게 됩니다. URI에 자동으로 접두어 /pub 이 붙습니다.
    @MessageMapping("/chat/message")
    public void message(MessageRequestDto requestDto, WsUser wsUser) {
        // 메시지 저장
        MessageResponseDto responseDto = chatMessageService.saveMessage(requestDto, wsUser);
        // 메시지 송신
        chatMessageService.sendMessage(requestDto, responseDto);
    }

    // 커넥트와 디스커넥트 시에는 이 주소로 IN과 OUT의 type으로 요청을 보냅니다.
    @MessageMapping("/chat/connect-status")
    public void connectStatus(MessageRequestDto requestDto) {
        // 동시접속자수 검증
        chatMessageService.setConnectedStatus(requestDto);
    }
}
