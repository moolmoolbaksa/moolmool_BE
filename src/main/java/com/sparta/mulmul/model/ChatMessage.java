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

    public static ChatMessage createOf(MessageRequestDto requestDto, Long roomId) {

        ChatMessage message = new ChatMessage();

        message.roomId = roomId;
        message.senderId = requestDto.getUserId();
        message.message = requestDto.getMessage();
        message.isRead = requestDto.getIsRead();

        return message;
    }

    public void read() {
        this.isRead = true;
    }

}
