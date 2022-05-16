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
            case CONNECT: accessor.setUser(checkVaild(accessor)); break; // User는 커넥트에서 한 번만 설정해 줍니다.
            case SEND: checkVaild(accessor); break; // 메시지 전송에서도 사용자를 검증합니다.
            default: break;
        }

        return message;
    }

    private WsUser checkVaild(StompHeaderAccessor accessor){

        WsUser wsUser;

        try {
            String token = extractor.extract(accessor.getFirstNativeHeader("Authorization"));
            Long userId = jwtDecoder.decodeTokenByUserId(token);
            String nickname = jwtDecoder.decodeTokenByNickname(token);
            wsUser = WsUser.createFrom(WsUserDto
                    .builder()
                    .userId(userId)
                    .nickname(nickname)
                    .name(UUID.randomUUID().toString())
                    .build()
            );

        } catch (Exception e) {
            throw new AccessDeniedException("StompHandler: 유효하지 않은 토큰입니다.");
        }

        return wsUser;
    }
}

