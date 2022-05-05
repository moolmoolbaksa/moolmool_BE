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
    private Long userId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MessageTypeEnum type;

    @Column(nullable = false)
    private Boolean isRead = false;

    public static ChatMessage fromMessageRequestDto(MessageRequestDto requestDto) {

        ChatMessage message = new ChatMessage();

        message.roomId = requestDto.getRoomId();
        message.userId = requestDto.getUserId();
        message.message = requestDto.getMessage();

        return message;
    }

}
