package com.sparta.mulmul.websocket.chatDto;

import com.sparta.mulmul.websocket.ChatMessage;
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
    private Boolean isRead = false;
    private MessageTypeEnum type;

    public static MessageResponseDto createOf(ChatMessage message, Long userId){

        MessageResponseDto responseDto = new MessageResponseDto();

        responseDto.senderId = userId;
        responseDto.messageId = message.getId();
        responseDto.message = message.getMessage();
        responseDto.date = message.getCreatedAt();
        responseDto.type = message.getType();

        return responseDto;
    }

    public static MessageResponseDto createFrom(ChatMessage message){

        MessageResponseDto responseDto = new MessageResponseDto();

        responseDto.senderId = message.getSenderId();
        responseDto.messageId = message.getId();
        responseDto.message = message.getMessage();
        responseDto.date = message.getCreatedAt();
        responseDto.type = message.getType();
        responseDto.isRead = true;

        return responseDto;

    }

}
