package com.sparta.mulmul.controller.websocketController;

import com.sparta.mulmul.dto.UserRequestDto;
import com.sparta.mulmul.dto.chat.MessageRequestDto;
import com.sparta.mulmul.dto.chat.MessageResponseDto;
import com.sparta.mulmul.security.jwt.HeaderTokenExtractor;
import com.sparta.mulmul.security.jwt.JwtDecoder;
import com.sparta.mulmul.service.chat.ChatMessageService;
import com.sparta.mulmul.service.chat.ChatRoomService;
import com.sparta.mulmul.websocket.WsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService messageService;
    private final ChatRoomService roomService;
    private final HeaderTokenExtractor extractor;
    private final JwtDecoder jwtDecoder;

    // 해당 어노테이션을 통해 웹소켓으로 pub 되는 모든 메시지를 처리하게 됩니다. URI에 자동으로 접두어 /pub 이 붙습니다.
    @MessageMapping("/chat/message")
    public void message(MessageRequestDto requestDto, @Header String Authorization) {

        WsUser wsUser; Long userId; String nickname;

        try{
            String token = extractor.extract(Authorization);
            userId = jwtDecoder.decodeTokenByUserId(token);
            nickname = jwtDecoder.decodeTokenByNickname(token);
        } catch (Exception e){
            throw new AccessDeniedException("ChatController: 유효하지 않은 토큰입니다.");
        }

        wsUser = WsUser.fromUserRequestDto(UserRequestDto.createOf(userId, nickname));

        // 메시지 저장, isRead를 전송받아 메시지 상태별로 읽음/안읽음 구분
        System.out.println("ChatController: 메시지를 전송받았습니다.");
        MessageResponseDto responseDto = roomService.saveMessage(requestDto, wsUser); //DB에 저장
        System.out.println("ChatController: 메시지를 DB에 저장하는데 성공했습니다.");
        // 메시지 발신
        System.out.println("ChatController: 메시지 발송을 시작합니다.");
        roomService.sendMessage(requestDto.getRoomId(), wsUser.getUserId(), responseDto);// 메시지를 sub 주소로 발송해줌
        System.out.println("ChatController: 메시지를 발송을 완료했습니다.");
    }

    // 커넥트와 디스커넥트 시에는 이 주소로 IN과 OUT의 type으로 요청을 보냅니다.
    @MessageMapping("/chat/connect-status")
    public void connectStatus(MessageRequestDto requestDto) {
        System.out.println("ChatController: 채팅방 정원이 꽉 찼는지 검증을 시작합니다.(NORMAL, FULL) (\"/chat/connect-status\")");
        // 동시접속자수 검증
        messageService.setConnectedStatus(requestDto);
        System.out.println("ChatController: 동시접속 검증메시지 전달이 끝났습니다. (\"/chat/connect-status\")");
    }
}
