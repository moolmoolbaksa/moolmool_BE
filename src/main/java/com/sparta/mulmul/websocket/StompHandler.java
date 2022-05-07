package com.sparta.mulmul.websocket;

import com.sparta.mulmul.dto.UserRequestDto;
import com.sparta.mulmul.security.jwt.HeaderTokenExtractor;
import com.sparta.mulmul.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final HeaderTokenExtractor extractor;
    private final JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        assert accessor != null;

        System.out.println("StompHandler: 엑세스 Header의 이름 : " + accessor.getFirstNativeHeader("Authorization"));

        if(accessor.getCommand() == StompCommand.CONNECT) {

            try {
                String token = extractor.extract(accessor.getFirstNativeHeader("Authorization"));
                System.out.println("StompHandler: 헤더에서 토큰 추출 완료");
                Long userId = jwtDecoder.decodeTokenByUserId(token);
                String nickname = jwtDecoder.decodeTokenByNickname(token);
                System.out.println("StompHandler: 토큰 디코딩 완료");
                WsUser wsUser = WsUser.fromUserRequestDto(UserRequestDto.createOf(userId, nickname));
                System.out.println("StompHandler: WsUser 객체 생성");

                accessor.setUser(wsUser);
                System.out.println("StompHandler: StompHeaderAccessor에 Principal 설정");

            } catch (Exception e) {
                throw new AccessDeniedException("StompHandler: 유효하지 않은 토큰입니다.");
            }
        }

        return message;
    }
}

