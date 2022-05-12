package com.sparta.mulmul.model;

import com.sparta.mulmul.dto.chat.MessageRequestDto;
import com.sparta.mulmul.dto.chat.MessageTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter @Entity
@NoArgsConstructor
public class ChatMessage extends CreationDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MessageTypeEnum type;

    @Column(nullable = false)
    private Boolean isRead;

    public static ChatMessage createOf(MessageRequestDto requestDto, Long senderId) {

        ChatMessage message = new ChatMessage();

        message.roomId = requestDto.getRoomId();
        message.senderId = senderId;
        message.message = requestDto.getMessage();
        message.isRead = requestDto.getIsRead();
        message.type = requestDto.getType();

        return message;
    }

    public static ChatMessage createInitOf(Long roomId, Long senderId){

        ChatMessage message = new ChatMessage();

        message.roomId = roomId;
        message.senderId = senderId;
        message.message = "채팅방이 개설되었습니다.";
        message.isRead = false;
        message.type = MessageTypeEnum.STATUS;

        return message;
    }
}
