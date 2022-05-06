package com.sparta.mulmul.controller.websocketController;

import com.sparta.mulmul.dto.chat.MessageRequestDto;
import com.sparta.mulmul.dto.chat.MessageResponseDto;
import com.sparta.mulmul.service.chat.ChatMessageService;
import com.sparta.mulmul.service.chat.ChatRoomService;
import com.sparta.mulmul.websocket.WsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService messageService;
    private final ChatRoomService roomService;

    // 해당 어노테이션을 통해 웹소켓으로 pub 되는 모든 메시지를 처리하게 됩니다. URI에 자동으로 접두어 /pub 이 붙습니다.
    @MessageMapping("/chat/message")
    public void message(MessageRequestDto requestDto, WsUser wsUser) {
        // 메시지 저장, isRead를 전송받아 메시지 상태별로 읽음/안읽음 구분
        MessageResponseDto responseDto = roomService.saveMessage(requestDto, wsUser); //DB에 저장
        // 메시지 발신
        roomService.sendMessage(requestDto.getRoomId(), wsUser.getUserId(), responseDto);// 메시지를 sub 주소로 발송해줌
    }

    // 커넥트와 디스커넥트 시에는 이 주소로 IN과 OUT의 type으로 요청을 보냅니다.
    @MessageMapping("/chat/connect-status")
    public void connectStatus(MessageRequestDto requestDto) {
        // 동시접속자수 검증
        messageService.setConnectedStatus(requestDto);
    }
}
