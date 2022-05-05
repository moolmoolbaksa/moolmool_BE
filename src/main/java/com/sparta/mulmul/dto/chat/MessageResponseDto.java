package com.sparta.mulmul.dto.chat;

import com.sparta.mulmul.model.ChatMessage;
import com.sparta.mulmul.websocket.WsUser;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MessageResponseDto {

    private Long messageId;
    private Long senderId;
    private String message;
    private LocalDateTime date;
    private final Boolean isRead = false;
    private MessageTypeEnum type;

    public static MessageResponseDto createOf(ChatMessage message, WsUser user){

        MessageResponseDto responseDto = new MessageResponseDto();

        responseDto.senderId = user.getUserId();
        responseDto.messageId = message.getId();
        responseDto.message = message.getMessage();
        responseDto.date = message.getCreatedAt();
        responseDto.type = message.getType();

        return responseDto;
    }
}
