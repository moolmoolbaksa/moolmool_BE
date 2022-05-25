package com.sparta.mulmul.websocket;

import com.sparta.mulmul.dto.WsUserDto;
import com.sparta.mulmul.security.jwt.HeaderTokenExtractor;
import com.sparta.mulmul.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor { // 이론상 웹소켓이 실행되기 전에 작동한다고 합니다.

    private final HeaderTokenExtractor extractor;
    private final JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        assert accessor != null;

        switch (Objects.requireNonNull(accessor.getCommand())){
            case CONNECT:
            case SUBSCRIBE:
            case SEND:
                checkVaild(accessor); break; // 유효성 검증
            default: break;
        }

        return message;
    }

    private void checkVaild(StompHeaderAccessor accessor){

        try {
            String token = extractor.extract(accessor.getFirstNativeHeader("Authorization"));
            Long userId = jwtDecoder.decodeTokenByUserId(token);
            String nickname = jwtDecoder.decodeTokenByNickname(token);

        } catch (Exception e) {
            throw new AccessDeniedException("StompHandler: 유효하지 않은 토큰입니다.");
        }

    }
}

